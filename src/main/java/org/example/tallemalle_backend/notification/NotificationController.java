package org.example.tallemalle_backend.notification;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.notification.model.NotificationDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/list")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "5") int size) {
        NotificationDto.PageRes dto = notificationService.list(user.getIdx(), page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/summary")
    public ResponseEntity summary(@AuthenticationPrincipal AuthUserDetails user){
        List<NotificationDto.ReadTop5Res> result = notificationService.summary(user);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/readonly/{idx}")
    public ResponseEntity readOnly(@AuthenticationPrincipal AuthUserDetails user,
                                   @PathVariable Long idx){
        NotificationDto.ReadOnlyRes result = notificationService.readOnly(user, idx);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/readall")
    public ResponseEntity readAll(@AuthenticationPrincipal AuthUserDetails user){
        notificationService.readAll(user);
        return ResponseEntity.ok("모두 읽음");
    }
}
