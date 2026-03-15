package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();

        Recruit recruit = dto.toEntity(realUser);

        // 이미 방에 접속 중이거나, 방장이면 반환

        // 매칭 엔티티 생성
        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status("ACTIVE")
                .build();

        recruit.getParticipations().add(participation);
        realUser.setCurrentRecruit(recruit);
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

    // TODO: Slice로 페이징 처리 필요
    public List<RecruitDto.ListRes> search(Double swLat, Double swLng, Double neLat, Double neLng) {
        List<Recruit> recruitList = recruitRepository.findRecruitsInBounds(swLat, swLng, neLat, neLng);
        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }

    // TODO: 예외 처리
    @Transactional
    public boolean join(AuthUserDetails user, Long recruitIdx) {
        // 모집글 찾아오기 TODO: 비관적 락
        Recruit recruit = recruitRepository.findById(recruitIdx).orElseThrow();

        // 모집에 참여하고 싶은 유저 조회
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();

        // 방장이 방에 입장하려고 하는 경우
        if(recruit.getOwner().getIdx().equals(realUser.getIdx())) {
            return false;
        }

        // 모집글 인원이 FULL인데 입장하려는 경우
        if(recruit.getStatus().equals(RecruitStatus.FULL)) {
            return false;
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
        // 모집 인원 추가된거 적용
        realUser.setCurrentRecruit(recruit);

        realUser.setStatus("JOINED");

        // 소켓 전송 Dto 생성
        RecruitDto.ListRes updatedDto = RecruitDto.ListRes.from(recruit);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "updateRecruit");
        message.put("payload", updatedDto);

        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);

        // 인원이 다 차면 소켓으로 기사님한테 전송 및 모집 마감
        if (recruit.getCurrentCapacity().equals(recruit.getMaxCapacity())) {
            // 모집 마감
            recruit.setStatus(RecruitStatus.FULL);
            // 기사님들 한테 모집 완료 되어 콜 잡으라고 전송
            simpMessagingTemplate.convertAndSend("/topic/complete", "EW_CALL_ADDED");
        }
        // 성공 반환
        return true;
    }

    @Transactional
    public boolean leave(AuthUserDetails user, Long recruitIdx) {
        Recruit recruit = recruitRepository.findById(recruitIdx).orElseThrow();
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();
        Participation participation = participationRepository.findByUserIdxAndRecruitIdx(realUser.getIdx(), recruit.getIdx()).orElseThrow();

        // TODO: 방장이 방을 나갈 때 처리 필요
        if(recruit.getOwner().getIdx().equals(user.getIdx())) {
            return false;
        }

        // 꽉 찬 방이었다면 다시 모집 중으로 변경
        if(recruit.getStatus() == RecruitStatus.FULL) {
            recruit.setStatus(RecruitStatus.RECRUITING);
        }

        // 모집 참여 취소로 상태 변경
        participation.setStatus("CANCELED");

        // 모집글 내부 인원 감소
        recruit.decreaseCapacity();

        // 유저 상태 다시 IDLE로 변경
        realUser.setStatus("IDLE");

        // 유저의 recruit_id를 다시 null로 변경
        realUser.setCurrentRecruit(null);

        // 변경된 모집글 정보를 소켓으로 전송
        RecruitDto.ListRes updatedDto = RecruitDto.ListRes.from(recruit);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "updateRecruit");
        message.put("payload", updatedDto);

        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);

        return true;
    }
}
