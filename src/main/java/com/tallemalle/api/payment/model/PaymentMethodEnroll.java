package com.tallemalle.api.payment.model;

import java.util.List;

public class PaymentMethodEnroll {
    public static class Request {
        private String userId;
        private String alias;
        private String billingKey;
        private Boolean asDefault;

        public Request() {
        }

        public Request(String userId, String alias, String billingKey, Boolean asDefault) {
            this.userId = userId;
            this.alias = alias;
            this.billingKey = billingKey;
            this.asDefault = asDefault;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getBillingKey() {
            return billingKey;
        }

        public void setBillingKey(String billingKey) {
            this.billingKey = billingKey;
        }

        public Boolean getAsDefault() {
            return asDefault;
        }

        public void setAsDefault(Boolean asDefault) {
            this.asDefault = asDefault;
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

        public List<PaymentMethod> getPaymentMethods() {
            return paymentMethods;
        }

        public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }

        public static PaymentMethodEnroll.Response from(PaymentMethodList.Response res) {
            return new Response(res.getDefaultMethodId(), res.getPaymentMethods());
        }
    }
}
