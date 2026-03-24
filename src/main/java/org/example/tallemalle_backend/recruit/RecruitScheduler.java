package org.example.tallemalle_backend.recruit;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RecruitScheduler {

    private static final int DEPARTURE_EXPIRE_GRACE_MINUTES = 20;

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
        
        // 출발 시각 기준 grace 경과 후에도 진행되지 않은 방 정리
        LocalDateTime limitTime = now.minusMinutes(DEPARTURE_EXPIRE_GRACE_MINUTES);
        List<RecruitStatus> targetStatuses = List.of(RecruitStatus.RECRUITING, RecruitStatus.FULL, RecruitStatus.CALLING);
        List<Recruit> expiredList = recruitRepository.findExpiredRecruits(targetStatuses, limitTime);

        for (Recruit r : expiredList) {
            try {
                // 연결된 최신 콜이 대기/취소/수락 상태라면 만료 처리
                callService.expireCallFromRecruit(r);

                // Soft Delete 처리
                r.cancelRecruit();

                // 방에 있던 유저들 전부 대기 상태(IDLE)로 방출
                r.getParticipations().forEach(p -> {
                    p.getUser().changeToIdle();
                    p.cancel();
                });

                // 더티체킹에 의존하지 않고 즉시 반영(디버깅/검증 용이)
                recruitRepository.saveAndFlush(r);

                // 모집글 삭제 이벤트 발행
                eventPublisher.publishEvent(new RecruitEvents.DeletedEvent(r.getIdx()));
            } catch (Exception e) {
                log.warn("만료 정리 실패 recruitIdx={}, reason={}", r.getIdx(), e.getMessage(), e);
            }
        }
    }
}
