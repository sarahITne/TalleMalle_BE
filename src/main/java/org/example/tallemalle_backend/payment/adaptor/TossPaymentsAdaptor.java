package org.example.tallemalle_backend.payment.adaptor;

import org.example.tallemalle_backend.payment.data.dto.TossDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Component
public class TossPaymentsAdaptor {

    private final WebClient webClient;

    public TossPaymentsAdaptor(@Value("${project.payment.toss}") String secretKey) {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        this.webClient = WebClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", "Basic " + encodedSecretKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public TossDto.IssueBillingKeyResponse issueBillingKey(TossDto.IssueBillingKeyRequest dto) {
        return webClient.post()
                .uri("/billing/authorizations/issue")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(TossDto.IssueBillingKeyResponse.class)
                .block();
    }

    public TossDto.RevokeBillingKeyResponse revokeBillingKey(TossDto.RevokeBillingKeyRequest dto) {
        return webClient.delete()
                .uri("/billing/{billingKey}", dto.getBillingKey())
                .retrieve()
                .bodyToMono(TossDto.RevokeBillingKeyResponse.class)
                .block();
    }

    public TossDto.ChargePerUserResponse chargePerUser(TossDto.ChargePerUserRequest dto) {
        return webClient.post()
                .uri("/billing/{billingKey}", dto.getBillingKey())
                .bodyValue(dto.toRequestBody())
                .retrieve()
                .bodyToMono(TossDto.ChargePerUserResponse.class)
                .block();
    }

    public TossDto.RefundTransactionResponse refundTransaction(TossDto.RefundTransactionRequest dto) {
        return webClient.post()
                .uri("/payments/{paymentKey}/cancel", dto.getPaymentKey())
                .bodyValue(dto.toRequestBody())
                .retrieve()
                .bodyToMono(TossDto.RefundTransactionResponse.class)
                .block();
    }
}
