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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ParticipationRepository participationRepository;

    // TODO: Socket 통신 연결 필요
    public void reg(AuthUserDetails user, RecruitDto.RegReq dto) {
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();

        Recruit recruit = dto.toEntity(realUser);

        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status("ACTIVE")
                .build();

        recruit.getParticipations().add(participation);
        realUser.setCurrentRecruit(recruit);
        recruitRepository.save(recruit);
    }

    // TODO: Slice로 페이징 처리 필요
    public List<RecruitDto.ListRes> list() {
        List<Recruit> recruitList = recruitRepository.findAll();

        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }

    // TODO: 예외 처리
    @Transactional
    public boolean join(AuthUserDetails user, Long recruitIdx) {
        // 모집글 찾아오기 TODO: 비관적 락
        Recruit recruit = recruitRepository.findById(recruitIdx).orElseThrow();

        // 인원이 꽉 차면 false 반환
        if (recruit.getCurrentCapacity() >= recruit.getMaxCapacity()) {
            return false;
        }

        // 모집에 참여하고 싶은 유저 조회
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();

        // 중복 참여 여부 확인
        if (participationRepository.existsByUserAndRecruit(realUser, recruit)) {
            return false;
        }

        // 매칭 엔티티 생성
        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status("ACTIVE")
                .build();

        // 모집 인원 + 1
        recruit.setCurrentCapacity(recruit.getCurrentCapacity() + 1);
        // 모집 인원 추가된거 적용
        realUser.setCurrentRecruit(recruit);
        // 매칭 엔티티 저장
        participationRepository.save(participation);

        // 인원이 다 차면 소켓으로 기사님한테 전송 및 모집 마감
        if (recruit.getCurrentCapacity() == recruit.getMaxCapacity()) {
            // 모집 마감
            recruit.setStatus(RecruitStatus.FULL);
            // 기사님들 한테 모집 완료 되어 콜 잡으라고 전송
            simpMessagingTemplate.convertAndSend("/topic/complete", "EW_CALL_ADDED");
        }
        // 성공 반환
        return true;
    }
}
