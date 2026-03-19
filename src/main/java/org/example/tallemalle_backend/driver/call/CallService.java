package org.example.tallemalle_backend.driver.call;

import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.notification.NotificationService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.infrastructure.KakaoMobilityService;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallDto;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.participation.model.Participation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {
    private final CallRepository callRepository;
    private final KakaoMobilityService kakaoMobilityService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;
    private final DriverUserRepository driverUserRepository;


    public List<CallDto.ListRes> list() {
        List<Call> callList = callRepository.findByStatusIn(List.of(CallStatus.WAITING, CallStatus.CANCELED));
        return callList.stream().map(CallDto.ListRes::from).toList();
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
        Call call = callRepository.findByDriverIdxAndStatus(driverIdx, CallStatus.ACCEPTED).orElseThrow();

        return CallDto.DetailRes.from(call);
    }

    @Transactional
    public void acceptCall(Long callIdx, Long driverIdx) {
        boolean alreadyAccepted = callRepository.existsByDriverIdxAndStatus(driverIdx, CallStatus.ACCEPTED);
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
        Driver driver = driverUserRepository.findById(driverIdx).orElseThrow();
        String notificationContents = recruit.getStartPointName() + " → " + recruit.getDestPointName() + " 운행에 " + driver.getName() + " 기사님이 배정되었습니다.";

        // 참여 중(ACTIVE)인 모든 유저에게 알림 보내기
        for (Participation participation : recruit.getParticipations()) {
            if (participation.isActive()) {
                notificationService.createNotification(
                        participation.getUser(),
                        "matching",
                        "운행 확정",
                        notificationContents
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
        String notificationContents = recruit.getStartPointName() + " → " + recruit.getDestPointName() + " 운행이 종료되었습니다!";

        // 참여 중(ACTIVE)인 모든 유저에게 알림 보내기
        for (Participation participation : recruit.getParticipations()) {
            if (participation.isActive()) {
                notificationService.createNotification(
                        participation.getUser(),
                        "matching",
                        "운행 종료",
                        notificationContents
                );
            }
        }
    }

    public List<CallDto.HistoryRes> getHistory(Long driverIdx) {
        List<Call> callList = callRepository.findAllByDriverIdxAndStatus(driverIdx, CallStatus.COMPLETED);
        return callList.stream().map(CallDto.HistoryRes::from).toList();
    }

    @Transactional(readOnly = true)
    public CallDto.SettlementRes getSettlement(Long callIdx) {
        Call call = callRepository.findById(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));
        return CallDto.SettlementRes.from(call);
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

    public void notifyNewCall() {
        simpMessagingTemplate.convertAndSend("/topic/complete", "EW_CALL_ADDED");
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
