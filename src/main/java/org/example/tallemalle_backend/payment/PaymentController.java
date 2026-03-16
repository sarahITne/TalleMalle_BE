package org.example.tallemalle_backend.payment;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.payment.data.dto.PaymentDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

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
}
