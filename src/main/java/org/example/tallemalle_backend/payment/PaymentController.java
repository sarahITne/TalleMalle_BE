package org.example.tallemalle_backend.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.payment.data.dto.PaymentDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment API", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "결제 키 요청", description = "사용자의 고유 결제 키(Customer Key)를 응답으로 반환합니다.")
    @GetMapping("/key")
    public ResponseEntity customerKey(@AuthenticationPrincipal AuthUserDetails user) {
        if (user == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }
        return ResponseEntity.ok(BaseResponse.success(paymentService.customerKey(user)));
    }

    @Operation(summary = "기본 결제 수단 변경", description = "사용자의 기본 결제 수단을 변경합니다.")
    @PatchMapping("/default-billing")
    public ResponseEntity defaultBilling(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam Long billingIdx) {
        if (user == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }
        return ResponseEntity.ok(BaseResponse.success(paymentService.defaultBilling(user, billingIdx)));
    }

    @Operation(summary = "결제 수단 등록", description = "토스 페이먼츠로부터 전달받은 인증 키(Auth Key)와 사용자 고유 결제 키(Customer Key)를 통해 빌링 키(Billing Key)를 발급 받습니다.")
    @GetMapping("/enroll")
    public ResponseEntity enroll(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestParam String customerKey,
            @RequestParam String authKey) {

        if (user == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }

        PaymentDto.EnrollRequest dto = PaymentDto.EnrollRequest.builder()
                .customerKey(customerKey)
                .authKey(authKey)
                .build();

        return ResponseEntity.ok(BaseResponse.success(paymentService.enroll(user, dto)));
    }
    @Operation(summary = "결제 수단 삭제", description = "등록된 결제 수단을 제거합니다.")
    @DeleteMapping("/billing/{idx}")
    public ResponseEntity revoke(
            @AuthenticationPrincipal AuthUserDetails user,
            @PathVariable Long idx) {

        if (user == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }

        PaymentDto.RevokeRequest dto = PaymentDto.RevokeRequest.builder()
                .ownerIdx(user.getIdx())
                .billingIdx(idx)
                .build();

        return ResponseEntity.ok(BaseResponse.success(paymentService.revoke(dto)));
    }

    @Operation(summary = "결제 수단 목록", description = "특정 사용자가 등록한 모든 결제 수단을 조회합니다.")
    @GetMapping("/billing")
    public ResponseEntity list(@AuthenticationPrincipal AuthUserDetails user) {
        if (user == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }
        return ResponseEntity.ok(BaseResponse.success(paymentService.list(user.getIdx())));
    }

    @Operation(summary = "결제", description = "모집에 참여한 사용자에게 등록된 결제 수단으로 결제를 수행합니다.")
    @PostMapping("/charge")
    public ResponseEntity charge(
            @AuthenticationPrincipal AuthDriverDetails driver,
            @RequestBody PaymentDto.ChargeRequest dto) {

        if (driver == null) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_UNAUTHENTICATED_USER);
        }

        return ResponseEntity.ok(BaseResponse.success(paymentService.charge(driver.getIdx(), dto)));
    }
}
