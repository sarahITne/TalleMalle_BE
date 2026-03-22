package org.example.tallemalle_backend.driver.call;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.driver.call.model.CallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/call")
public class CallController {
    private final CallService callService;

    @GetMapping("/list")
    public ResponseEntity<Page<CallDto.ListRes>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(callService.list(pageable));
    }

    @GetMapping("/read/{callIdx}")
    public ResponseEntity read(@PathVariable Long callIdx){
        CallDto.DetailRes result = callService.read(callIdx);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/readmycall")
    public ResponseEntity read(@AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        CallDto.DetailRes result = callService.readMyCall(driverIdx);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/accept/{callIdx}")
    public ResponseEntity accept(@PathVariable Long callIdx, @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.acceptCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 수락");
    }

    @PatchMapping("/driving/{callIdx}")
    public ResponseEntity driving(@PathVariable Long callIdx, @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.startDrivingCall(callIdx, driverIdx);
        return ResponseEntity.ok("운행 시작");
    }

    @PatchMapping("/complete/{callIdx}")
    public ResponseEntity complete(@PathVariable Long callIdx, @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.completeCall(callIdx, driverIdx);
        return ResponseEntity.ok("운행 완료");
    }

    @GetMapping("/settlement/{callIdx}")
    public ResponseEntity settlement(@PathVariable Long callIdx){
        CallDto.SettlementRes result = callService.getSettlement(callIdx);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<CallDto.HistoryPageRes> history(
            @AuthenticationPrincipal AuthDriverDetails driver,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(callService.getHistory(driver.getIdx(), pageable));
    }

    @PatchMapping("/cancel/{callIdx}")
    public ResponseEntity cancel(@PathVariable Long callIdx, @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.cancelCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 취소");
    }
}
