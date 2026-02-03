package com.tallemalle.api.payment.controller;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.payment.model.PaymentEnroll;
import com.tallemalle.api.payment.model.PaymentList;
import com.tallemalle.api.utils.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PaymentController implements Controller {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (req.getRequestURI().contains("list")) {
                PaymentList.Request parsedReq = JsonParser.from(req, PaymentList.Request.class);
                PaymentList.Response res = paymentService.list(parsedReq);
                return BaseResponse.success(res);
            } else if (req.getRequestURI().contains("enroll")) {
                PaymentEnroll.Request parsedReq = JsonParser.from(req, PaymentEnroll.Request.class);
                PaymentEnroll.Response res = paymentService.enroll(parsedReq);
                return BaseResponse.success(res);
            }
            return BaseResponse.fail(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
