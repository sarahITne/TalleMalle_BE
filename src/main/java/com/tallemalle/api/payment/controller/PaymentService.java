package com.tallemalle.api.payment.controller;

import com.tallemalle.api.payment.model.PaymentMethodEnroll;
import com.tallemalle.api.payment.model.PaymentMethodList;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentMethodList.Response list(PaymentMethodList.Request req) {
        return paymentRepository.list(req);
    }

    public PaymentMethodEnroll.Response enroll(PaymentMethodEnroll.Request req) {
        return paymentRepository.enroll(req);
    }
}
