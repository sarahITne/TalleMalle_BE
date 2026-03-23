package org.example.tallemalle_backend.driver.call;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Driver Call API", description = "드라이버 콜 목록·상세·수락·운행·정산·이력 API")
@RequestMapping("/call")
@RestController
@RequiredArgsConstructor
public class CallController {
    private final CallService callService;

    @Operation(summary = "콜 목록 조회", description = "페이징된 콜 목록을 조회하는 기능")
    @GetMapping("/list")
    public ResponseEntity<Page<CallDto.ListRes>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(callService.list(pageable));
    }

    @Operation(summary = "콜 상세 조회", description = "특정 콜 Idx로 상세 정보를 조회하는 기능")
    @GetMapping("/read/{callIdx}")
    public ResponseEntity read(@Parameter(description = "콜 Idx") @PathVariable Long callIdx){
        CallDto.DetailRes result = callService.read(callIdx);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 배정 콜 조회", description = "로그인한 드라이버에게 배정된 현재 콜을 조회하는 기능")
    @GetMapping("/readmycall")
    public ResponseEntity read(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        CallDto.DetailRes result = callService.readMyCall(driverIdx);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "콜 수락", description = "특정 콜을 수락하는 기능")
    @PatchMapping("/accept/{callIdx}")
    public ResponseEntity accept(
            @Parameter(description = "콜 Idx") @PathVariable Long callIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.acceptCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 수락");
    }

    @Operation(summary = "운행 시작", description = "수락한 콜에 대해 운행 시작 상태로 전환하는 기능")
    @PatchMapping("/driving/{callIdx}")
    public ResponseEntity driving(
            @Parameter(description = "콜 Idx") @PathVariable Long callIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.startDrivingCall(callIdx, driverIdx);
        return ResponseEntity.ok("운행 시작");
    }

    @Operation(summary = "운행 완료", description = "콜 운행을 완료 처리하는 기능")
    @PatchMapping("/complete/{callIdx}")
    public ResponseEntity complete(
            @Parameter(description = "콜 Idx") @PathVariable Long callIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.completeCall(callIdx, driverIdx);
        return ResponseEntity.ok("운행 완료");
    }

    @Operation(summary = "정산 정보 조회", description = "특정 콜의 정산 정보를 조회하는 기능")
    @GetMapping("/settlement/{callIdx}")
    public ResponseEntity settlement(@Parameter(description = "콜 Idx") @PathVariable Long callIdx){
        CallDto.SettlementRes result = callService.getSettlement(callIdx);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "콜 이력 조회", description = "로그인한 드라이버의 콜 이력을 페이징 조회하는 기능")
    @GetMapping("/history")
    public ResponseEntity<CallDto.HistoryPageRes> history(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(callService.getHistory(driver.getIdx(), pageable));
    }

    @Operation(summary = "콜 취소", description = "드라이버가 콜을 취소하는 기능")
    @PatchMapping("/cancel/{callIdx}")
    public ResponseEntity cancel(
            @Parameter(description = "콜 Idx") @PathVariable Long callIdx,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDriverDetails driver){
        Long driverIdx = driver.getIdx();
        callService.cancelCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 취소");
    }
}
