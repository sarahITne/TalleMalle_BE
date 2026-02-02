package com.tallemalle.api.payment.model;

import java.util.List;

/**
 * 결제 수단 목룍 조회
 */
public class PaymentMethodList {

    public static class Request {
        private String userId;

        public Request(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class Response {
        private Integer defaultMethodId;
        private List<PaymentMethod> paymentMethods;

        public Response(Integer defaultMethodId, List<PaymentMethod> paymentMethods) {
            this.defaultMethodId = defaultMethodId;
            this.paymentMethods = paymentMethods;
        }

        public Integer getDefaultMethodId() {
            return defaultMethodId;
        }

        public void setDefaultMethodId(Integer defaultMethodId) {
            this.defaultMethodId = defaultMethodId;
        }

        public List<PaymentMethod> getPaymentInstruments() {
            return paymentMethods;
        }

        public void setPaymentInstruments(List<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }
    }
}
