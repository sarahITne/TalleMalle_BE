package com.tallemalle.api.payment.controller;
import com.tallemalle.api.payment.model.PaymentIssue;
import com.tallemalle.api.utils.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TossPaymentsAdaptor {
    private final String secretKey = System.getenv("TOSS_SK");
    public PaymentIssue.Response issueBillingKey(PaymentIssue.Request req) throws IOException, InterruptedException {
        String requestBody = "{\"authKey\":\"" + req.getAuthKey() + "\",\"customerKey\":\"" + req.getCustomerKey() + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/issue"))
                .header("Authorization", "Basic "+secretKey)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println(response.body());
            return JsonParser.from(response, PaymentIssue.Response.class);
        }
        return null;
    }
}
