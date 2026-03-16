package org.example.tallemalle_backend.driver.call;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.infrastructure.KakaoMobilityService;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallDto;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {
    private final CallRepository callRepository;
    private final KakaoMobilityService kakaoMobilityService;


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
            callRepository.save(call);
        }

        return CallDto.DetailRes.from(call, call.getEstimatedFare());
    }

    public CallDto.DetailRes readMyCall(Long driverIdx) {
        Call call = callRepository.findByDriverIdx(driverIdx).orElseThrow();

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
