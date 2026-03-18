package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ParticipationRepository participationRepository;

    // TODO: Socket 통신 연결 필요
    @Transactional
    public void reg(AuthUserDetails user, RecruitDto.RegReq dto) {
        // 유저 정보 가져오기
        User realUser = userRepository.findById(user.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        Recruit recruit = dto.toEntity(realUser);

        // 이미 방에 접속 중이면 반환
        boolean isAlreadyJoined = realUser.getParticipations().stream()
                .anyMatch(p -> "ACTIVE".equals(p.getStatus()));

        if (isAlreadyJoined) {
            throw new BaseException(BaseResponseStatus.ALREADY_JOINED);
        }

        // 매칭 엔티티 생성
        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status("ACTIVE")
                .build();

        recruit.getParticipations().add(participation);
        realUser.setStatus("OWNER");

        // DB에 모집글 저장
        Recruit savedRecruit = recruitRepository.save(recruit);

        // 소켓으로 보낼 DTO 생성
        RecruitDto.ListRes responseDto = RecruitDto.ListRes.from(savedRecruit);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "newRecruit");
        message.put("payload", responseDto);

        // 소켓으로 전송
        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);
    }

    // TODO: Slice로 페이징 처리 필요
    public List<RecruitDto.ListRes> list() {
        List<Recruit> recruitList = recruitRepository.findAll();
        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }

    // 모집글 상세 조회
    public RecruitDto.DetailRes detail(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );
        return RecruitDto.DetailRes.from(recruit);
    }

    // TODO: Slice로 페이징 처리 필요
    public List<RecruitDto.ListRes> search(Double swLat, Double swLng, Double neLat, Double neLng) {
        List<Recruit> recruitList = recruitRepository.findRecruitsInBounds(swLat, swLng, neLat, neLng);
        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }

    // TODO: 예외 처리
    @Transactional
    public boolean join(AuthUserDetails user, Long recruitIdx) {
        // 모집글 찾아오기 TODO: 비관적 락
        Recruit recruit = recruitRepository.findById(recruitIdx).orElseThrow(
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
            if ("ACTIVE".equals(existingParticipation.getStatus())) {
                return false;
            } else {
                // 과거에 나갔다가 다시 들어오는 경우 상태만 Update
                existingParticipation.setStatus("ACTIVE");
            }
        } else {
            // 아예 처음 참여하는 경우 새로 만들어서 저장
            Participation newParticipation = Participation.builder()
                    .user(realUser)
                    .recruit(recruit)
                    .status("ACTIVE")
                    .build();
            participationRepository.save(newParticipation);
        }

        // 모집 인원 + 1
        recruit.setCurrentCapacity(recruit.getCurrentCapacity() + 1);

        realUser.setStatus("JOINED");

        // 인원이 다 차면
        if (recruit.getCurrentCapacity().equals(recruit.getMaxCapacity())) {
            // 모집 마감
            recruit.setStatus(RecruitStatus.FULL);
        }

        // 소켓 전송 Dto 생성
        RecruitDto.ListRes updatedDto = RecruitDto.ListRes.from(recruit);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "updateRecruit");
        message.put("payload", updatedDto);

        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);

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
                User pUser = p.getUser();
                pUser.setStatus("IDLE");
            });

            // Soft Delete로 처리
            recruit.setStatus(RecruitStatus.END);

            // 소켓 통신으로 방이 없어졌다고 알림
            Map<String, Object> message = new HashMap<>();
            message.put("type", "deleteRecruit");
            message.put("payload", recruitIdx);

            simpMessagingTemplate.convertAndSend("/topic/all-calls", message);

            return true;

        }

        Participation participation = participationRepository.findByUserIdxAndRecruitIdx(realUser.getIdx(), recruit.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA)
        );

        // 진짜 방을 못 나가는 경우
        if (recruit.getStatus() == RecruitStatus.CALLING || recruit.getStatus() == RecruitStatus.DRIVING) {
            throw new BaseException(BaseResponseStatus.ALREADY_CALLED);
        }

        // 꽉 찬 방(FULL)에서 누군가 나가는 경우 다시 누군가 들어올 수 있게 모집 중(RECRUITING)으로 변경
        if (recruit.getStatus() == RecruitStatus.FULL) {
            recruit.setStatus(RecruitStatus.RECRUITING);
        }

        // 모집 참여 취소로 상태 변경
        participation.setStatus("CANCELED");

        // 모집글 내부 인원 감소
        recruit.decreaseCapacity();

        // 유저 상태 다시 IDLE로 변경
        realUser.setStatus("IDLE");

        // 변경된 모집글 정보를 소켓으로 전송
        RecruitDto.ListRes updatedDto = RecruitDto.ListRes.from(recruit);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "updateRecruit");
        message.put("payload", updatedDto);

        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);

        return true;
    }

    // 60초(1분)마다 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void recruitTimeCheckScheduler() {
        LocalDateTime now = LocalDateTime.now();

        // 출발 시간이 지났고, 인원이 꽉 찬(FULL) 방 찾기
        List<Recruit> readyToCallList = recruitRepository.findReadyToCall(now);

        for (Recruit r : readyToCallList) {
            // 다음 1분 뒤에 또 호출하는 것 방지
            r.setStatus(RecruitStatus.CALLING);
            // 기사님 호출
            simpMessagingTemplate.convertAndSend("/topic/complete", "EW_CALL_ADDED");
        }

        // 출발 시간 기준 20분이 지났는데 출발하지 못한 방 찾기
        LocalDateTime limitTime = now.minusMinutes(20);
        List<Recruit> expiredList = recruitRepository.findExpiredRecruits(limitTime);

        for (Recruit r : expiredList) {
            // Soft Delete 처리
            r.setStatus(RecruitStatus.END);

            // 방에 있던 유저들 전부 대기 상태(IDLE)로 방출
            r.getParticipations().forEach(p -> {
                p.getUser().setStatus("IDLE");
                p.setStatus("CANCELED");
            });

            // 소켓으로 해당 방이 폭파되었음을 클라이언트에 알림
            Map<String, Object> message = new HashMap<>();
            message.put("type", "deleteRecruit");
            message.put("payload", r.getIdx());
            simpMessagingTemplate.convertAndSend("/topic/all-calls", message);
        }
    }
}
