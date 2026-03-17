package org.example.tallemalle_backend.payment;

import jakarta.transaction.Transactional;
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

import java.util.ArrayList;
import java.util.List;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.PAYMENT_BILLING_INVALID_OWNER;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;
    private final TossPaymentsAdaptor tossPaymentsAdaptor;

    @Transactional
    public PaymentDto.EnrollResponse enroll(AuthUserDetails userDetails, PaymentDto.EnrollRequest dto) {

        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );

        //user.validateCustomerKey(dto.getCustomerKey());

        TossDto.issueBillingKeyResponse response = tossPaymentsAdaptor.issueBillingKey(dto.toIssueBillingKeyDto());

        Billing billing = response.toEntity(user);

        billing = billingRepository.save(billing);

//        if (user.getDefaultBillingIdx() == null) {
//            user.setDefaultBillingIdx(billing.getIdx());
//        }

        return PaymentDto.EnrollResponse.builder()
                .billingGroup(getBillingGroup(user))
                .build();
    }

    @Transactional
    public PaymentDto.RevokeResponse revoke(PaymentDto.RevokeRequest dto) {
        Billing billing = billingRepository.findById(dto.getBillingIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_NOT_EXIST)
        );

        if (!billing.getOwner().getIdx().equals(dto.getOwnerIdx())) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_BILLING_INVALID_OWNER);
        }

        User user = userRepository.findById(dto.getOwnerIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_NOT_EXIST)
        );

        List<Billing> allBillings = billingRepository.findAllByOwnerOrderByCreatedAtAsc(user);
        if (allBillings.size() <= 1) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_BILLING_REQUIRED);
        }

        TossDto.revokeBillingKeyRequest req = TossDto.revokeBillingKeyRequest.builder()
                .billingKey(billing.getBillingKey())
                .build();

        tossPaymentsAdaptor.revokeBillingKey(req);

        billingRepository.delete(billing);

//        if (user.getDefaultBillingIdx().equals(billing.getIdx())) {
//            Billing newDefault = allBillings.stream()
//                    .filter(b -> !b.getIdx().equals(billing.getIdx()))
//                    .findFirst()
//                    .orElseThrow(() -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_REQUIRED));
//            user.setDefaultBillingIdx(newDefault.getIdx());
//        }

        return PaymentDto.RevokeResponse.builder()
                .billingGroup(getBillingGroup(user))
                .build();
    }

    public PaymentDto.ListResponse list(Long userIdx) {

        User user = userRepository.findById(userIdx).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_NOT_EXIST)
        );

        return PaymentDto.ListResponse.builder()
                .billingGroup(getBillingGroup(user))
                .build();
    }

    private PaymentDto.BillingGroupRes getBillingGroup(User owner) {
        List<Billing> billings = billingRepository.findAllByOwner(owner);

        PaymentDto.BillingRes defaultBilling = null;
        List<PaymentDto.BillingRes> otherBillings = new ArrayList<>();

        for (Billing elem : billings) {
//            if (elem.getIdx().equals(owner.getDefaultBillingIdx())) {
//                defaultBilling = PaymentDto.BillingRes.fromEntity(elem);
//            } else {
//                otherBillings.add(PaymentDto.BillingRes.fromEntity(elem));
//            }
        }
        return PaymentDto.BillingGroupRes.builder()
                .defaultBilling(defaultBilling)
                .otherBillings(otherBillings)
                .build();
    }
}
