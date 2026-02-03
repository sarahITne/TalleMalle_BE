package com.tallemalle.api.payment.model.dto;

import com.tallemalle.api.payment.model.entity.Payment;

import java.util.List;

public class PaymentEnroll {
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

        public static PaymentEnroll.Response from(PaymentList.Response res) {
            return new Response(res.getDefaultMethodId(), res.getPaymentMethods());
        }
    }
}
