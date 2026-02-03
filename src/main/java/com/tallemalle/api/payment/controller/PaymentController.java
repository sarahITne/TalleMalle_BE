package com.tallemalle.api.payment.controller;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.payment.model.PaymentMethodEnroll;
import com.tallemalle.api.payment.model.PaymentMethodList;
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
                PaymentMethodList.Request parsedReq = JsonParser.from(req, PaymentMethodList.Request.class);
                PaymentMethodList.Response res = paymentService.list(parsedReq);
                return BaseResponse.success(res);
            } else if (req.getRequestURI().contains("enroll")) {
                PaymentMethodEnroll.Request parsedReq = JsonParser.from(req, PaymentMethodEnroll.Request.class);
                PaymentMethodEnroll.Response res = paymentService.enroll(parsedReq);
                return BaseResponse.success(res);
            }
            return BaseResponse.fail(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
