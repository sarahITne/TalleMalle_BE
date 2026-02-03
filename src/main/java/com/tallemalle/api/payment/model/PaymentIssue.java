package com.tallemalle.api.payment.model;

import lombok.*;

public class PaymentIssue {

    public static class Request {
        private String customerKey;
        private String authKey;

        public Request() {
        }

        public Request(String customerKey, String authKey) {
            this.customerKey = customerKey;
            this.authKey = authKey;
        }

        public String getCustomerKey() {
            return customerKey;
        }

        public void setCustomerKey(String customerKey) {
            this.customerKey = customerKey;
        }

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }

        public static PaymentIssue.Request from(PaymentEnroll.Request enrollReq) {
            return new Request(enrollReq.getCustomerKey(), enrollReq.getAuthKey());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private String mId;
        private String customerKey;
        private String authenticatedAt;
        private String method;
        private String billingKey;
        private String cardCompany;
        private String cardNumber;
        private Card card;

        @Getter
        @NoArgsConstructor
        public static class Card {
            private String issuerCode;
            private String acquirerCode;
            private String number;
            private String cardType;
            private String ownerType;
        }
    }
}
