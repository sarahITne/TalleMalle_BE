package org.example.tallemalle_backend.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.notification.NotificationService;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.payment.adaptor.TossPaymentsAdaptor;
import org.example.tallemalle_backend.payment.data.BillingRepository;
import org.example.tallemalle_backend.payment.data.OrderRepository;
import org.example.tallemalle_backend.payment.data.TransactionRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final BillingRepository billingRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final DriverUserRepository driverUserRepository;
    private final ParticipationRepository participationRepository;
    private final TossPaymentsAdaptor tossPaymentsAdaptor;
    private final org.example.tallemalle_backend.recruit.RecruitRepository recruitRepository;
    private final NotificationService notificationService;

    public PaymentDto.CustomerKeyResponse customerKey(AuthUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );
        return PaymentDto.CustomerKeyResponse.builder()
                .customerKey(user.getCustomerKey())
                .build();
    }

    @Transactional
    public PaymentDto.DefaultBillingResponse defaultBilling(AuthUserDetails userDetails, Long billingIdx) {
        User user = userRepository.findById(userDetails.getIdx()).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER)
        );

        Billing billing = billingRepository.findById(billingIdx).orElseThrow(
                () -> BaseException.from(BaseResponseStatus.PAYMENT_BILLING_NOT_EXIST)
        );

        if (!billing.getOwner().equals(user)) {
            throw BaseException.from(BaseResponseStatus.PAYMENT_BILLING_INVALID_OWNER);
        }

        user.setDefaultBilling(billing);
        return PaymentDto.DefaultBillingResponse.builder().build();
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

        driverUserRepository.findById(driverIdx).orElseThrow();

        Recruit recruit = recruitRepository.findById(dto.getRecruitIdx()).orElseThrow();

        List<Participation> participations = participationRepository.findAllByRecruit_Idx(dto.getRecruitIdx()).stream()
                .filter(Participation::isActive)
                .sorted(Comparator.comparing(Participation::getIdx))
                .collect(Collectors.toList());

        if (participations.isEmpty()) {
            throw BaseException.from(BaseResponseStatus.REQUEST_ERROR);
        }

        int total = dto.getCommission() + dto.getServiceFee();
        int n = participations.size();
        int baseShare = total / n;
        int remainder = total % n;

        // 모든 사용자의 기본 결제 수단 유효성 검증
        for (Participation participation : participations) {
            if (participation.getUser().getDefaultBilling() == null) {
                throw BaseException.from(BaseResponseStatus.PAYMENT_DEFAULT_BILLING_REQUIRED);
            }
        }

        // 성공한 결제 내역을 저장할 리스트
        List<Transaction> successTransactions = new ArrayList<>();

        // 결제에 대한 주문 생성 및 결제 시도 (정산 화면과 동일하게 1/N + 나머지 원 단위 배분)
        try {
            for (int i = 0; i < participations.size(); i++) {
                Participation participation = participations.get(i);
                int amount = baseShare + (i < remainder ? 1 : 0);
                User user = participation.getUser();
                Order order = Order.builder()
                        .billing(user.getDefaultBilling())
                        .amount(amount)
                        .user(user)
                        .recruit(recruit)
                        .build();
                orderRepository.save(order);

                try {
                    TossDto.ChargePerUserResponse res = tossPaymentsAdaptor.chargePerUser(TossDto.ChargePerUserRequest.fromEntity(order));
                    
                    // 주문 성공 상태 변경
                    order.setStatus(org.example.tallemalle_backend.payment.common.OrderStatus.SUCCESS);
                    
                    // 결제 내역 생성 및 저장
                    Transaction transaction = res.toEntity(order);
                    transactionRepository.save(transaction);
                    successTransactions.add(transaction);
                } catch (Exception e) {
                    order.setStatus(org.example.tallemalle_backend.payment.common.OrderStatus.FAILURE);
                    throw e;
                }
            }
            sendPaymentCompletedNotifications(recruit, participations);
        } catch (Exception e) {
            // 하나라도 실패하면 성공한 결제들 취소(환불) 처리
            for (Transaction transaction : successTransactions) {
                TossDto.RefundTransactionRequest request = TossDto.RefundTransactionRequest.builder()
                        .paymentKey(transaction.getPaymentKey())
                        .cancelReason("결제 실패로 인한 환불")
                        .build();
                tossPaymentsAdaptor.refundTransaction(request);
                
                transaction.getOrder().setStatus(org.example.tallemalle_backend.payment.common.OrderStatus.CANCELED);
            }
        }
        return PaymentDto.ChargeResponse.builder().build();
    }

    /**
     * 모집 매칭 알림과 동일하게 DB·STOMP·(설정 시) Web Push. 알림 실패는 결제 성공에 영향 없음.
     */
    private void sendPaymentCompletedNotifications(Recruit recruit, List<Participation> participations) {
        Long recruitIdx = recruit.getIdx();
        String title = "결제 완료";
        String contents = "운행 요금 결제가 완료되었습니다.";
        for (Participation p : participations) {
            try {
                notificationService.createNotification(
                        p.getUser(),
                        "matching",
                        title,
                        contents,
                        recruitIdx
                );
            } catch (Exception ex) {
                log.warn("결제 완료 알림 실패 userIdx={}, reason={}", p.getUser().getIdx(), ex.getMessage());
            }
        }
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
