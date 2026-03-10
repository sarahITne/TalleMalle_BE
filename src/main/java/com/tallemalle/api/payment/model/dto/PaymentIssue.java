package com.tallemalle.api.payment.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class PaymentIssue {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String customerKey;
        private String authKey;

        public static Request from(PaymentEnroll.Request enrollReq) {
            return new Request(enrollReq.getCustomerKey(), enrollReq.getAuthKey());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private String mId;
        private String customerKey;

        private OffsetDateTime authenticatedAt;

        private String method;
        private String billingKey;
        private String cardCompany;
        private String cardNumber;
        private Card card;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Card {
            private String issuerCode;
            private String acquirerCode;
            private String number;
            private String cardType;
            private String ownerType;
        }
    }
}