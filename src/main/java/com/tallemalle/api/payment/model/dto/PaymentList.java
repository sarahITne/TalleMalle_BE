package com.tallemalle.api.payment.model.dto;

import com.tallemalle.api.payment.model.entity.Payment;

import java.util.List;

/**
 * 결제 수단 목룍 조회
 */
public class PaymentList {

    public static class Request {
        private String userId;

        public Request() {}
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
        private List<Payment> payments;

        public Response(Integer defaultMethodId, List<Payment> payments) {
            this.defaultMethodId = defaultMethodId;
            this.payments = payments;
        }

        public Integer getDefaultMethodId() {
            return defaultMethodId;
        }

        public void setDefaultMethodId(Integer defaultMethodId) {
            this.defaultMethodId = defaultMethodId;
        }

        public List<Payment> getPaymentMethods() {
            return payments;
        }

        public void setPaymentMethods(List<Payment> payments) {
            this.payments = payments;
        }
    }
}
