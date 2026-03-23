package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.driver.call.CallRepository;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.recruit.event.RecruitEvents;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CallRepository callRepository;
    private final DriverUserRepository driverUserRepository;

    @Transactional
    public void reg(AuthUserDetails user, RecruitDto.RegReq dto) {
        // 유저 정보 가져오기
        User realUser = userRepository.findById(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        Recruit recruit = dto.toEntity(realUser);

        // 이미 방에 접속 중이면 반환
        boolean isAlreadyJoined = realUser.getParticipations().stream().anyMatch(Participation::isActive);

        if (isAlreadyJoined) {
            throw new BaseException(BaseResponseStatus.ALREADY_JOINED);
        }

        // 매칭 엔티티 생성
        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status(ParticipationStatus.ACTIVE)
                .build();

        recruit.getParticipations().add(participation);

        realUser.changeToOwner();

        // DB에 모집글 저장
        Recruit savedRecruit = recruitRepository.save(recruit);

        // 소켓 발송 이벤트 발행
        eventPublisher.publishEvent(new RecruitEvents.CreatedEvent(RecruitDto.ListRes.from(savedRecruit)));
    }

    // 사용자의 현재 화면의 남서쪽/북동쪽 위경도 좌표 받아서 처리
    @Transactional(readOnly = true)
    public Slice<RecruitDto.ListRes> search(Double swLat, Double swLng, Double neLat, Double neLng, Pageable pageable) {
        // DB 레벨에서 status 필터링 및 지정된 개수만 조회 (Slice 사용으로 count 쿼리 발생 안 함)
        Slice<Recruit> recruitSlice = recruitRepository.findActiveRecruitsInBounds(
                swLat, swLng, neLat, neLng, RecruitStatus.END, pageable);

        // 바로 DTO로 매핑 후 반환
        return recruitSlice.map(RecruitDto.ListRes::from);
    }

    // 모집글 참여
    @Transactional
    public boolean join(AuthUserDetails user, Long recruitIdx) {
        Recruit recruit = recruitRepository.findByIdForUpdate(recruitIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        // 모집에 참여하고 싶은 유저 조회
        User realUser = userRepository.findById(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        // 방장이 방에 입장하려고 하는 경우
        if(recruit.getOwner().getIdx().equals(realUser.getIdx())) {
            throw new BaseException(BaseResponseStatus.ALREADY_JOINED);
        }

        // 모집글 인원이 FULL인데 입장하려는 경우
        if(recruit.getStatus().equals(RecruitStatus.FULL) || recruit.getCurrentCapacity() >= recruit.getMaxCapacity()) {
            throw new BaseException(BaseResponseStatus.RECRUIT_FULL);
        }

        // 중복 참여 여부 확인
        Optional<Participation> optParticipation = participationRepository.findByUserIdxAndRecruitIdx(realUser.getIdx(), recruit.getIdx());

        if (optParticipation.isPresent()) {
            Participation existingParticipation = optParticipation.get();
            // 이미 참여 중이면 거절
            if (existingParticipation.getStatus() == ParticipationStatus.ACTIVE) {
                return false;
            } else {
                // 과거에 나갔다가 다시 들어오는 경우 상태만 Update
                existingParticipation.activate();
            }
        } else {
            // 아예 처음 참여하는 경우 새로 만들어서 저장
            Participation newParticipation = Participation.builder()
                    .user(realUser)
                    .recruit(recruit)
                    .status(ParticipationStatus.ACTIVE)
                    .build();

            participationRepository.save(newParticipation);
        }

        realUser.changeToJoined();

        // 모집 인원 + 1
        recruit.addParticipant();

        // 인원이 다 차면
        if (recruit.getStatus() == RecruitStatus.FULL) {
            // 모집 확정 알림 소켓 발생 이벤트 발행
            eventPublisher.publishEvent(new RecruitEvents.FullEvent(recruit.getIdx(), recruit.getStartPointName(), recruit.getDestPointName()));
        }

        // 소켓 발생 이벤트 발행
        eventPublisher.publishEvent(new RecruitEvents.UpdatedEvent(RecruitDto.ListRes.from(recruit)));

        // 성공 반환
        return true;
    }

    @Transactional
    public boolean leave(AuthUserDetails user, Long recruitIdx) {
        Recruit recruit = recruitRepository.findById(recruitIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );
        User realUser = userRepository.findById(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        // 방장이 방을 나갈 때
        if(recruit.getOwner().getIdx().equals(user.getIdx())) {
            // 방에 참여 중인 모든 유저의 상태를 IDLE로 변경
            recruit.getParticipations().forEach(p -> {
                p.getUser().changeToIdle();
                // participtions 테이블에 status CANCELED로 변경
                p.cancel();
            });

            // Soft Delete로 처리
            recruit.setStatus(RecruitStatus.END);

            // 방 폭파 소켓 발송 로직 교체
            eventPublisher.publishEvent(new RecruitEvents.DeletedEvent(recruitIdx));

            return true;

        }

        Participation participation = participationRepository.findByUserIdxAndRecruitIdx(realUser.getIdx(), recruit.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        // 진짜 방을 못 나가는 경우
        if (recruit.getStatus() == RecruitStatus.CALLING || recruit.getStatus() == RecruitStatus.DRIVING) {
            throw new BaseException(BaseResponseStatus.ALREADY_CALLED);
        }

        // 모집 참여 취소로 상태 변경
        participation.cancel();

        // 모집글 내부 인원 감소
        recruit.removeParticipant();

        // 유저 상태 다시 IDLE로 변경
        realUser.changeToIdle();

        eventPublisher.publishEvent(new RecruitEvents.UpdatedEvent(RecruitDto.ListRes.from(recruit)));

        // 퇴장 이벤트 발행
        eventPublisher.publishEvent(new RecruitEvents.UserLeftEvent(recruitIdx, realUser.getIdx(), realUser.getName()));

        return true;
    }

    // 모집글 상세 조회
    public RecruitDto.DetailRes detail(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );
        String driverName = null;
        Integer estimatedFare = 0;
        Integer myFare = 0;
        if (recruit.getStatus() == RecruitStatus.DRIVING || recruit.getStatus() == RecruitStatus.END) {
            Optional<Call> callOpt = callRepository.findTopByRecruit_IdxAndStatusInOrderByIdDesc(
                    recruitId,
                    List.of(CallStatus.ACCEPTED, CallStatus.DRIVING, CallStatus.COMPLETED)
            );
            if (callOpt.isPresent()) {
                Call call = callOpt.get();
                if (call.getDriverIdx() != null) {
                    Optional<Driver> driverOpt = driverUserRepository.findById(call.getDriverIdx());
                    if (driverOpt.isPresent()) {
                        driverName = driverOpt.get().getName();
                    }
                }
                estimatedFare = call.getEstimatedFare();
                if (recruit.getCurrentCapacity() != null && recruit.getCurrentCapacity() > 0) {
                    myFare = estimatedFare / recruit.getCurrentCapacity();
                }
            }
        }
        return RecruitDto.DetailRes.from(recruit, driverName, estimatedFare, myFare);
    }
}
