package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.call.CallService;
import org.example.tallemalle_backend.recruit.event.RecruitEvents;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitScheduler {

    private static final int DEPARTURE_EXPIRE_GRACE_MINUTES = 12;

    private final RecruitRepository recruitRepository;
    private final CallService callService;
    private final ApplicationEventPublisher eventPublisher;

    // 60초(1분)마다 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void recruitTimeCheckScheduler() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime notExpiredAfter = now.minusMinutes(DEPARTURE_EXPIRE_GRACE_MINUTES);

        // 정원 FULL · 콜 미생성 · 출발 전(만료 직전까지) → 드라이버가 출발 시각 전에 콜을 보고 수락 및 이동할 수 있게 함
        List<Recruit> readyToCallList = recruitRepository.findReadyToCall(RecruitStatus.FULL, notExpiredAfter);

        for (Recruit r : readyToCallList) {
            // 다음 1분 뒤에 또 호출하는 것 방지
            r.setStatus(RecruitStatus.CALLING);
            // 기사님 호출
            callService.createCallFromRecruit(r);

            // 모집글 업데이트 이벤트 발행
            eventPublisher.publishEvent(new RecruitEvents.UpdatedEvent(RecruitDto.ListRes.from(r)));
        }

        // 트랜잭션 커밋 이후 소켓 전송 (저장 실패 시 소켓이 안 나가도록 분리)
        if (!readyToCallList.isEmpty()) {
            callService.notifyNewCall();
        }

        // 출발 시각 기준 grace 경과 후에도 진행되지 않은 방 정리
        LocalDateTime limitTime = now.minusMinutes(DEPARTURE_EXPIRE_GRACE_MINUTES);
        List<RecruitStatus> targetStatuses = List.of(RecruitStatus.RECRUITING, RecruitStatus.FULL, RecruitStatus.CALLING);
        List<Recruit> expiredList = recruitRepository.findExpiredRecruits(targetStatuses, limitTime);

        for (Recruit r : expiredList) {
            // Soft Delete 처리
            r.cancelRecruit();

            // 방에 있던 유저들 전부 대기 상태(IDLE)로 방출
            r.getParticipations().forEach(p -> {
                p.getUser().changeToIdle();
                p.cancel();
            });

            // 모집글 삭제 이벤트 발행
            eventPublisher.publishEvent(new RecruitEvents.DeletedEvent(r.getIdx()));
        }
    }
}
