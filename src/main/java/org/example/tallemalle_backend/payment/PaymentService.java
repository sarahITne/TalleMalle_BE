package org.example.tallemalle_backend.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.payment.adaptor.TossPaymentsAdaptor;
import org.example.tallemalle_backend.payment.data.BillingRepository;
import org.example.tallemalle_backend.payment.data.OrderRepository;
import org.example.tallemalle_backend.payment.data.dto.PaymentDto;
import org.example.tallemalle_backend.payment.data.dto.TossDto;
import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.payment.data.entity.Order;
import org.example.tallemalle_backend.payment.data.entity.Transaction;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BillingRepository billingRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DriverUserRepository driverUserRepository;
    private final ParticipationRepository participationRepository;
    private final TossPaymentsAdaptor tossPaymentsAdaptor;

    public PaymentDto.CustomerKeyResponse customerKey(AuthUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );
        return PaymentDto.CustomerKeyResponse.builder()
                .customerKey(user.getCustomerKey())
                .build();
    }
    @Transactional
    public PaymentDto.EnrollResponse enroll(AuthUserDetails userDetails, PaymentDto.EnrollRequest dto) {

        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );

        validateCustomerKey(user, dto.getCustomerKey());

        TossDto.IssueBillingKeyResponse response = tossPaymentsAdaptor.issueBillingKey(dto.toIssueBillingKeyDto());

        Billing billing = response.toEntity(user);

        billing = billingRepository.save(billing);

        if (user.getDefaultBilling() == null) {
            user.setDefaultBilling(billing);
        }

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

        TossDto.RevokeBillingKeyRequest req = TossDto.RevokeBillingKeyRequest.builder()
                .billingKey(billing.getBillingKey())
                .build();

        tossPaymentsAdaptor.revokeBillingKey(req);

        billingRepository.delete(billing);

        if (user.getDefaultBilling().equals(billing)) {
            Billing newDefault = allBillings.stream()
                    .filter(b -> !b.getIdx().equals(billing.getIdx()))
                    .findFirst()
                    .orElseThrow(() -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_REQUIRED));
            user.setDefaultBilling(newDefault);
        }

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

    @Transactional
    public PaymentDto.ChargeResponse charge(Long driverIdx, PaymentDto.ChargeRequest dto) {

        Driver driver = driverUserRepository.findById(driverIdx).orElseThrow();

        List<Participation> participations = participationRepository.findAllByRecruit_Idx(dto.getRecruitIdx());

        int amount = (dto.getCommission() + dto.getServiceFee()) / participations.size();

        // 모든 사용자의 기본 결제 수단 유효성 검증
        for (Participation participation : participations) {
            if (participation.getUser().getDefaultBilling() == null) {
                throw BaseException.from(BaseResponseStatus.PAYMENT_DEFAULT_BILLING_REQUIRED);
            }
        }

        // 성공한 주문을 저장할 리스트
        List<Transaction> successTransaction = new ArrayList<>();

        // 결제에 대한 주문 생성 및 결제 시도
        try {
            for (Participation participation : participations) {
                User user = participation.getUser();
                Order order = Order.builder()
                        .billing(user.getDefaultBilling())
                        .amount(amount)
                        .user(user)
                        .build();
                orderRepository.save(order);

                TossDto.ChargePerUserResponse res = tossPaymentsAdaptor.chargePerUser(TossDto.ChargePerUserRequest.fromEntity(order));
                // 결제 내역 생성
                successTransaction.add(res.toEntity(order));
            }
        } catch (Exception e) {
            for (Transaction transaction : successTransaction) {
                TossDto.RefundTransactionRequest request = TossDto.RefundTransactionRequest.builder()
                        .paymentKey(transaction.getPaymentKey())
                        .cancelReason("결제 실패로 인한 환불")
                        .build();
                tossPaymentsAdaptor.refundTransaction(request);
            }
        }
        return PaymentDto.ChargeResponse.builder().build();
    }



    private PaymentDto.BillingGroupRes getBillingGroup(User owner) {
        List<Billing> billings = billingRepository.findAllByOwner(owner);

        PaymentDto.BillingRes defaultBilling = null;
        List<PaymentDto.BillingRes> otherBillings = new ArrayList<>();

        for (Billing elem : billings) {
            if (elem.equals(owner.getDefaultBilling())) {
                defaultBilling = PaymentDto.BillingRes.fromEntity(elem);
            } else {
                otherBillings.add(PaymentDto.BillingRes.fromEntity(elem));
            }
        }
        return PaymentDto.BillingGroupRes.builder()
                .defaultBilling(defaultBilling)
                .otherBillings(otherBillings)
                .build();
    }

    private void validateCustomerKey(User user, String customerKey) {
        if (!user.getCustomerKey().equals(customerKey)) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_CUSTOMER_KEY);
        }
    }
}
