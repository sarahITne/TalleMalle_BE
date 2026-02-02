package com.tallemalle.api.payment.controller;

import com.tallemalle.api.payment.model.PaymentMethod;
import com.tallemalle.api.payment.model.PaymentMethodEnroll;
import com.tallemalle.api.payment.model.PaymentMethodList;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentMethodList.Response list(PaymentMethodList.Request req) throws Exception {
        Integer defaultMethodId = -1;
        Integer userId = Integer.parseInt(req.getUserId());
        List<PaymentMethod> paymentMethods = new ArrayList<>();

        try (ResultSet defaultRs = paymentRepository.fetchDefault(userId)) {
            if (defaultRs.next()) {
                defaultMethodId = defaultRs.getInt("payment_id");
            }
        }
        try (ResultSet listRs = paymentRepository.list(userId)) {
            while (listRs.next()) {
                paymentMethods.add(new PaymentMethod(listRs.getInt("id"), listRs.getString("alias")));
            }
        }
        return new PaymentMethodList.Response(defaultMethodId, paymentMethods);
    }

    public PaymentMethodEnroll.Response enroll(PaymentMethodEnroll.Request req) throws Exception {
        Integer userId = Integer.parseInt(req.getUserId());
        try (ResultSet enrollRs = paymentRepository.enroll(userId, req.getAlias(), req.getBillingKey())) {
            if (enrollRs.next() && req.getAsDefault()) {
                Integer paymentId = enrollRs.getInt("insert_id");
                paymentRepository.setDefault(userId, paymentId);
            }
        }
        return PaymentMethodEnroll.Response.from(list(PaymentMethodList.Request.from(req)));
    }
}
