package org.example.tallemalle_backend.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "알림 목록 조회",
            description = "로그인한 사용자의 알림을 페이지 단위로 조회합니다. 최신순으로 반환됩니다.\n반환: BaseResponse<PageRes>"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"success\":true,\"code\":2000,\"message\":\"요청 성공\",\"result\":{\"content\":[{\"idx\":1,\"title\":\"운행 시작\",\"isRead\":false}]}}"))
            ),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0") @RequestParam(required = true, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(required = true, defaultValue = "5") int size) {
        NotificationDto.PageRes dto = notificationService.list(user.getIdx(), page, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @Operation(summary = "알림 요약(상위)", description = "최근 알림 요약(예: 상위 5건)을 조회합니다.\n반환: BaseResponse<List<ReadTop5Res>>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(examples = @ExampleObject(value = "{\"success\":true,\"code\":2000,\"message\":\"요청 성공\",\"result\":[{\"idx\":8,\"title\":\"결제 완료\"}]}"))),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @GetMapping("/summary")
    public ResponseEntity<?> summary(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user){
        List<NotificationDto.ReadTop5Res> result = notificationService.summary(user);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "알림 개별 읽음", description = "특정 알림을 읽음 처리합니다.\n반환: BaseResponse<ReadOnlyRes>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공", content = @Content(examples = @ExampleObject(value = "{\"success\":true,\"code\":2000,\"message\":\"요청 성공\",\"result\":{\"idx\":123,\"isRead\":true}}"))),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @PatchMapping("/readonly/{idx}")
    public ResponseEntity<?> readOnly(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "알림 ID", example = "123") @PathVariable Long idx){
        NotificationDto.ReadOnlyRes result = notificationService.readOnly(user, idx);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "알림 전체 읽음", description = "로그인한 사용자의 모든 알림을 읽음 처리합니다.\n반환: BaseResponse<String>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 읽음 처리 성공", content = @Content(examples = @ExampleObject(value = "{\"success\":true,\"code\":2000,\"message\":\"요청 성공\",\"result\":\"모두 읽음\"}"))),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @PatchMapping("/readall")
    public ResponseEntity<?> readAll(@Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user){
        notificationService.readAll(user);
        return ResponseEntity.ok(BaseResponse.success("모두 읽음"));
    }
}
