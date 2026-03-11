package org.example.tallemalle_backend.driver;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.model.CallDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/driver")
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/calls")
    public ResponseEntity list(){
        List<CallDto.ListRes> result = driverService.list();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/call/{callIdx}")
    public ResponseEntity read(@PathVariable Long callIdx){
        CallDto.DetailRes result = driverService.read(callIdx);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/accept/{callIdx}")
    public ResponseEntity accept(@PathVariable Long callIdx, @AuthenticationPrincipal AuthUserDetails driver){
        Long driverIdx = driver.getIdx();
        driverService.acceptCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 수락");
    }

    @PatchMapping("/cancel/{callIdx}")
    public ResponseEntity cancel(@PathVariable Long callIdx, @AuthenticationPrincipal AuthUserDetails driver){
        Long driverIdx = driver.getIdx();
        driverService.cancelCall(callIdx, driverIdx);
        return ResponseEntity.ok("콜 취소");
    }
}
