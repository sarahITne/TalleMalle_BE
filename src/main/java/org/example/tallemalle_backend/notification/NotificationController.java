package org.example.tallemalle_backend.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.notification.model.NotificationDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification API", description = "알림 목록·요약·읽음 처리 API")
@RequestMapping("/notification")
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림을 페이지 단위로 조회하는 기능")
    @GetMapping("/list")
    public ResponseEntity list(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "페이지 번호(0부터)") @RequestParam(required = true, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(required = true, defaultValue = "5") int size) {
        NotificationDto.PageRes dto = notificationService.list(user.getIdx(), page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @Operation(summary = "알림 요약(상위)", description = "최근 알림 요약(예: 상위 5건)을 조회하는 기능")
    @GetMapping("/summary")
    public ResponseEntity summary(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user){
        List<NotificationDto.ReadTop5Res> result = notificationService.summary(user);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "알림 개별 읽음", description = "특정 알림을 읽음 처리하는 기능")
    @PatchMapping("/readonly/{idx}")
    public ResponseEntity readOnly(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "알림 Idx") @PathVariable Long idx){
        NotificationDto.ReadOnlyRes result = notificationService.readOnly(user, idx);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "알림 전체 읽음", description = "로그인한 사용자의 모든 알림을 읽음 처리하는 기능")
    @PatchMapping("/readall")
    public ResponseEntity readAll(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user){
        notificationService.readAll(user);
        return ResponseEntity.ok("모두 읽음");
    }
}
