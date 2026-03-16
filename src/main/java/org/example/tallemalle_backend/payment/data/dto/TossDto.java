package org.example.tallemalle_backend.payment.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.payment.data.entity.Billing;

import java.util.Map;

public class TossDto {

    @Builder
    @Getter
    public static class issueBillingKeyRequest {
        private String authKey;
        private String customerKey;
    }

    @Builder
    @Getter
    public static class issueBillingKeyResponse {
        private static final Map<String, String> CARD_MAP = Map.of(
                "11", "국민", "31", "비씨", "41", "신한", "51", "삼성",
                "61", "현대", "71", "롯데", "91", "농협", "33", "우리"
        );

        private String billingKey;
        private Card card;

        public Billing toEntity() {
            return Billing.builder()
                    .billingKey(this.billingKey)
                    .alias(createAlias())
                    .build();
        }

        private String createAlias() {
            return CARD_MAP.getOrDefault(card.issuerCode, "기타") + "(" + this.card.getNumber().substring(0, 4) + ")";
        }
    }

    @Builder
    @Getter
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }
}
