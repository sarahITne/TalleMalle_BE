package org.example.tallemalle_backend.driver.call;

import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.notification.NotificationService;
import org.example.tallemalle_backend.recruit.event.RecruitEvents;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.infrastructure.KakaoMobilityService;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallDto;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {
    private final CallRepository callRepository;
    private final KakaoMobilityService kakaoMobilityService;
    private final NotificationService notificationService;
    private final DriverUserRepository driverUserRepository;
    private final ParticipationRepository participationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<CallDto.ListRes> list(Pageable pageable) {
        return callRepository
                .findByStatusIn(List.of(CallStatus.WAITING, CallStatus.CANCELED), pageable)
                .map(CallDto.ListRes::from);
    }

    public CallDto.DetailRes read(Long callIdx) {
        Call call = callRepository.findById(callIdx).orElseThrow();

        if (call.getEstimatedFare() == 0) {
            DirectionInfo direction = kakaoMobilityService.getDirections(call);
            int fare = calculateTaxiFare(direction.getDistance() / 1000.0, direction.getDuration() / 60);
            call.setEstimatedFare(fare);
            call.setEstimatedDistance(direction.getDistance() / 1000.0);
            call.setEstimatedDuration(direction.getDuration() / 60);

            callRepository.save(call);
        }

        return CallDto.DetailRes.from(call, call.getEstimatedFare());
    }

    public CallDto.DetailRes readMyCall(Long driverIdx) {
        Call call = callRepository.findByDriverIdxAndStatusIn(driverIdx, List.of(CallStatus.ACCEPTED, CallStatus.DRIVING)).orElseThrow();

        return CallDto.DetailRes.from(call);
    }

    @Transactional
    public void acceptCall(Long callIdx, Long driverIdx) {
        boolean alreadyAccepted = callRepository.findByDriverIdxAndStatusIn(driverIdx, List.of(CallStatus.ACCEPTED, CallStatus.DRIVING)).isPresent();
        if (alreadyAccepted) {
            throw new IllegalStateException("이미 진행 중인 운행이 있습니다.");
        }

        Call call = callRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("콜이 존재하지 않습니다."));

        if (call.getStatus() != CallStatus.WAITING && call.getStatus() != CallStatus.CANCELED) {
            throw new IllegalStateException("이미 처리된 콜입니다.");
        }

        call.accept(driverIdx);

        // Notification에 알림 저장 로직
        Recruit recruit = call.getRecruit();
        recruit.setStatus(RecruitStatus.DRIVING);
        eventPublisher.publishEvent(new RecruitEvents.UpdatedEvent(RecruitDto.ListRes.from(recruit)));

        Driver driver = driverUserRepository.findById(driverIdx).orElseThrow();
        String notificationContents = recruit.getStartPointName() + " → " + recruit.getDestPointName() + " 운행에 " + driver.getName() + " 기사님이 배정되었습니다.";

        // 참여 중(ACTIVE)인 모든 유저에게 알림 보내기
        for (Participation participation : recruit.getParticipations()) {
            if (participation.isActive()) {
                notificationService.createNotification(
                        participation.getUser(),
                        "matching",
                        "운행 확정",
                        notificationContents,
                        recruit.getIdx()
                );
            }
        }
    }

    @Transactional
    public void startDrivingCall(Long callIdx, Long driverIdx) {
        Call call = callRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));

        if (!driverIdx.equals(call.getDriverIdx())) {
            throw new IllegalStateException("본인이 배정된 콜만 운행 시작할 수 있습니다.");
        }

        call.startDriving();

        // 참여 중(ACTIVE)인 모든 유저에게 알림 보내기
        Recruit recruit = call.getRecruit();
        String notificationContents = recruit.getStartPointName() + " → " + recruit.getDestPointName() + " 운행이 시작되었습니다!";

        for (Participation participation : recruit.getParticipations()) {
            if (participation.isActive()) {
                notificationService.createNotification(
                        participation.getUser(),
                        "matching",
                        "운행 시작",
                        notificationContents,
                        recruit.getIdx()
                );
            }
        }
    }

    @Transactional
    public void completeCall(Long callIdx, Long driverIdx) {
        Call call = callRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));

        if (!driverIdx.equals(call.getDriverIdx())) {
            throw new IllegalStateException("본인이 배정된 콜만 완료 처리할 수 있습니다.");
        }

        call.complete();

        // Notification에 알림 저장 로직
        Recruit recruit = call.getRecruit();
        recruit.setStatus(RecruitStatus.END);
        eventPublisher.publishEvent(new RecruitEvents.UpdatedEvent(RecruitDto.ListRes.from(recruit)));

        recruit.getParticipations().forEach(p -> {
            p.getUser().changeToIdle();
            // participtions 테이블에 status DONE으로 변경
            p.done();
        });

        String notificationContents = recruit.getStartPointName() + " → " + recruit.getDestPointName() + " 운행이 종료되었습니다!";

        // 참여 중(ACTIVE)인 모든 유저에게 알림 보내기
        for (Participation participation : recruit.getParticipations()) {
            if (participation.isActive()) {
                notificationService.createNotification(
                        participation.getUser(),
                        "matching",
                        "운행 종료",
                        notificationContents,
                        recruit.getIdx()
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public CallDto.HistoryPageRes getHistory(Long driverIdx, Pageable pageable) {
        Page<Call> page = callRepository.findAllByDriverIdxAndStatus(driverIdx, CallStatus.COMPLETED, pageable);
        Long sum = callRepository.sumEstimatedFareByDriverIdxAndStatus(driverIdx, CallStatus.COMPLETED);
        long totalFare = sum != null ? sum : 0L;
        return CallDto.HistoryPageRes.builder()
                .content(page.getContent().stream().map(CallDto.HistoryRes::from).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .number(page.getNumber())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .totalEstimatedFare(totalFare)
                .build();
    }

    @Transactional(readOnly = true)
    public CallDto.SettlementRes getSettlement(Long callIdx) {
        Call call = callRepository.findById(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));
        Recruit recruit = call.getRecruit();
        if (recruit == null) {
            return CallDto.SettlementRes.from(call, List.of(), null);
        }
        List<Participation> billable = participationRepository.findAllByRecruit_Idx(recruit.getIdx()).stream()
                .filter(p -> p.getStatus() != ParticipationStatus.CANCELED)
                .sorted(Comparator.comparing(Participation::getIdx))
                .toList();
        User fallback = billable.isEmpty() ? recruit.getOwner() : null;
        return CallDto.SettlementRes.from(call, billable, fallback);
    }

    @Transactional
    public void cancelCall(Long callIdx, Long driverIdx) {
        Call call = callRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));

        if (call.getDriverIdx() != null && !call.getDriverIdx().equals(driverIdx)) {
            throw new IllegalStateException("본인이 배정된 콜만 취소할 수 있습니다.");
        }

        call.cancel();
    }

    @Transactional
    public void createCallFromRecruit(Recruit recruit) {
        Call newCall = Call.builder()
                .recruit(recruit)
                .startLocation(recruit.getStartPointName())
                .endLocation(recruit.getDestPointName())
                .startLat(BigDecimal.valueOf(recruit.getStartLat()))
                .startLng(BigDecimal.valueOf(recruit.getStartLng()))
                .endLat(BigDecimal.valueOf(recruit.getDestLat()))
                .endLng(BigDecimal.valueOf(recruit.getDestLng()))
                .status(CallStatus.WAITING)
                .estimatedFare(0)
                .build();
        callRepository.save(newCall);
    }

    public void expireCallFromRecruit(Recruit recruit) {
        if (recruit == null || recruit.getIdx() == null) {
            return;
        }
        callRepository.findTopByRecruit_IdxAndStatusInOrderByIdDesc(
                recruit.getIdx(),
                List.of(CallStatus.WAITING, CallStatus.CANCELED, CallStatus.ACCEPTED)
        ).ifPresent(Call::expire);
    }

    // 예상 금액 계산 로직
    private int calculateTaxiFare(double distanceKm, int durationMinutes) {

        int baseFare = 4800;
        double baseDistanceKm = 1.6;

        int distanceFare = 0;
        if (distanceKm > baseDistanceKm) {
            distanceFare = (int) Math.ceil((distanceKm - baseDistanceKm) / 0.131) * 100;
        }

        // 실제 시간요금 (1분 = 200원)
        int timeFare = durationMinutes * 200;

        // 예상요금은 시간요금 50%만 반영
        int totalFare = baseFare + distanceFare + (timeFare / 2);

        return (totalFare / 100) * 100;
    }
}
