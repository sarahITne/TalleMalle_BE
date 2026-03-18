package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.call.CallService;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecruitScheduler {
    private final RecruitRepository recruitRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CallService callService;

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
            callService.createCallFromRecruit(r);
        }

        // 트랜잭션 커밋 이후 소켓 전송 (저장 실패 시 소켓이 안 나가도록 분리)
        if (!readyToCallList.isEmpty()) {
            callService.notifyNewCall();
        }

        // 출발 시간 기준 20분이 지났는데 출발하지 못한 방 찾기
        LocalDateTime limitTime = now.minusMinutes(20);
        List<Recruit> expiredList = recruitRepository.findExpiredRecruits(limitTime);

        for (Recruit r : expiredList) {
            // Soft Delete 처리
            r.cancelRecruit();

            // 방에 있던 유저들 전부 대기 상태(IDLE)로 방출
            r.getParticipations().forEach(p -> {
                p.getUser().changeToIdle();
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
