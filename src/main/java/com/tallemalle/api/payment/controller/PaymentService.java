package com.tallemalle.api.payment.controller;

import com.tallemalle.api.payment.model.PaymentIssue;
import com.tallemalle.api.payment.model.entity.Payment;
import com.tallemalle.api.payment.model.PaymentEnroll;
import com.tallemalle.api.payment.model.PaymentList;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentsAdaptor tossPaymentsAdaptor;

    public PaymentService(PaymentRepository paymentRepository, TossPaymentsAdaptor tossPaymentsAdaptor) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentsAdaptor = tossPaymentsAdaptor;
    }

    public PaymentList.Response list(PaymentList.Request req) throws Exception {
        Integer defaultMethodId = -1;
        Integer userId = Integer.parseInt(req.getUserId());
        List<Payment> payments = new ArrayList<>();

        try (ResultSet defaultRs = paymentRepository.fetchDefault(userId)) {
            if (defaultRs.next()) {
                defaultMethodId = defaultRs.getInt("payment_id");
            }
        }
        try (ResultSet listRs = paymentRepository.list(userId)) {
            while (listRs.next()) {
                //payments.add(new Payment(listRs.getInt("id"), listRs.getString("alias")));
            }
        }
        return new PaymentList.Response(defaultMethodId, payments);
    }

    public PaymentEnroll.Response enroll(PaymentEnroll.Request req) throws Exception {
        // 등록 요청을 받으면 TossPaymentsAdaptor로 빌링키 발행 요청
        PaymentIssue.Response res = tossPaymentsAdaptor.issueBillingKey(PaymentIssue.Request.from(req));
        // 빌링키 발행 요청에 대한 응답을 받아서 해당 응답에 대한 처리 후
        if (res != null) {
            System.out.println(res);
        } else {
            //paymentRepository.enroll(Payment.from())
        }
        // 정상 발행이면 PaymentRepository를 통해 실제로 DB에 CRUD
        // 발행이 안됐으면 그에 대한 응답
        //return PaymentEnroll.Response.from(list(Pay.Request.from(req)));
        return null;
    }
}
