package com.tallemalle.api.payment.model;

import java.util.List;

public class PaymentMethodRegister {
    public static class Request {
        private String userId;
        private String alias;
        private String billingKey;
        private Boolean asDefault;
    }
    public static class Response {
        private Integer defaultMethodId;
        private List<PaymentMethod> paymentMethods;
    }
}
