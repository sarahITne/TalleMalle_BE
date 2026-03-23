package org.example.tallemalle_backend.payment.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.payment.data.entity.Order;
import org.example.tallemalle_backend.payment.data.entity.Transaction;
import org.example.tallemalle_backend.user.model.User;

import java.util.Map;

public class TossDto {

    @Builder
    @Getter
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }

    @Builder
    @Getter
    public static class IssueBillingKeyRequest {
        private String authKey;
        private String customerKey;
    }

    @Builder
    @Getter
    public static class IssueBillingKeyResponse {
        private static final Map<String, String> CARD_MAP = Map.of(
                "11", "국민", "31", "비씨", "41", "신한", "51", "삼성",
                "61", "현대", "71", "롯데", "91", "농협", "33", "우리"
        );

        private String billingKey;
        private Card card;

        public Billing toEntity(User user) {
            return Billing.builder()
                    .billingKey(this.billingKey)
                    .alias(createAlias())
                    .owner(user)
                    .build();
        }

        private String createAlias() {
            return CARD_MAP.getOrDefault(card.issuerCode, "기타") + "(" + this.card.getNumber().substring(0, 4) + ")";
        }
    }
    @Builder
    @Getter
    public static class RevokeBillingKeyRequest {
        private String billingKey;
    }

    @Builder
    @Getter
    public static class RevokeBillingKeyResponse {

    }

    @Builder
    @Getter
    public static class ChargePerUserRequest {
        private String billingKey;
        private Integer amount;
        private String customerKey;
        private String orderId;
        private String orderName;

        public static ChargePerUserRequest fromEntity(Order order) {
            return ChargePerUserRequest.builder()
                    .amount(order.getAmount())
                    .billingKey(order.getBilling().getBillingKey())
                    .customerKey(order.getUser().getCustomerKey())
                    .orderId(order.getId().toString())
                    .orderName(order.getName())
                    .build();
        }

        public Map<String, Object> toRequestBody() {
            return Map.of(
                    "amount", this.amount,
                    "customerKey", this.customerKey,
                    "orderId", this.orderId,
                    "orderName", this.orderName
            );
        }
    }

    @Builder
    @Getter
    public static class ChargePerUserResponse {
        private String paymentKey;

        public Transaction toEntity(Order order) {
            return Transaction.builder()
                    .paymentKey(this.paymentKey)
                    .billing(order.getBilling())
                    .order(order)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class RefundTransactionRequest{
        private String paymentKey;
        private String cancelReason;

        public Map<String, Object> toRequestBody() {
            return Map.of(
                    "cancelReason", this.cancelReason
            );
        }
    }

    @Builder
    @Getter
    public static class RefundTransactionResponse {

    }
}
