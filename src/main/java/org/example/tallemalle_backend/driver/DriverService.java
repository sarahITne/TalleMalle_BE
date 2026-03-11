package org.example.tallemalle_backend.driver;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.model.Call;
import org.example.tallemalle_backend.driver.model.CallDto;
import org.example.tallemalle_backend.driver.model.CallStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    public List<CallDto.ListRes> list() {
        List<Call> callList = driverRepository.findByStatusIn(List.of(CallStatus.WAITING, CallStatus.CANCELED));
        return callList.stream().map(CallDto.ListRes::from).toList();
    }

    public CallDto.DetailRes read(Long callIdx) {
        Call call = driverRepository.findById(callIdx).orElseThrow();

        return CallDto.DetailRes.from(call);
    }

    @Transactional
    public void acceptCall(Long callIdx, Long driverIdx) {
        Call call = driverRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("콜이 존재하지 않습니다."));

        if (call.getStatus() != CallStatus.WAITING && call.getStatus() != CallStatus.CANCELED) {
            throw new IllegalStateException("이미 처리된 콜입니다.");
        }

        call.accept(driverIdx);
    }

    @Transactional
    public void cancelCall(Long callIdx, Long driverIdx) {
        Call call = driverRepository.findByIdWithLock(callIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콜입니다."));

        if (call.getDriverIdx() != null && !call.getDriverIdx().equals(driverIdx)) {
            throw new IllegalStateException("본인이 배정된 콜만 취소할 수 있습니다.");
        }

        call.cancel();
    }
}
