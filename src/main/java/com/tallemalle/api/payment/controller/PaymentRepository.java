package com.tallemalle.api.payment.controller;

import com.tallemalle.api.payment.model.PaymentMethodEnroll;
import com.tallemalle.api.payment.model.PaymentMethodList;

import javax.sql.DataSource;

public class PaymentRepository {
    private final DataSource ds;

    public PaymentRepository(DataSource ds) {
        this.ds = ds;
    }

    public PaymentMethodList.Response list(PaymentMethodList.Request req) {
        return new PaymentMethodList.Response(0, null);
    }

    public PaymentMethodEnroll.Response enroll(PaymentMethodEnroll.Request req) {
        return new PaymentMethodEnroll.Response(0, null);
    }
}
