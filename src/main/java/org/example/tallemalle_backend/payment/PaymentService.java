package org.example.tallemalle_backend.payment;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.payment.adaptor.TossPaymentsAdaptor;
import org.example.tallemalle_backend.payment.data.dto.PaymentDto;
import org.example.tallemalle_backend.payment.data.dto.TossDto;
import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final TossPaymentsAdaptor tossPaymentsAdaptor;

    public PaymentDto.EnrollResponse enroll(AuthUserDetails userDetails, PaymentDto.EnrollRequest dto) {

        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );

        user.validateCustomerKey(dto.getCustomerKey());

        TossDto.issueBillingKeyResponse response = tossPaymentsAdaptor.issueBillingKey(dto.toIssueBillingKeyDto());

        Billing billing = response.toEntity(user);

        billing = billingRepository.save(billing);

        return PaymentDto.EnrollResponse.builder()
                .idx(billing.getIdx())
                .alias(billing.getAlias())
                .build();
    }
}
