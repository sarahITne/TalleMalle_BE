package org.example.tallemalle_backend.payment.data.dto;

import lombok.Builder;
import lombok.Getter;

public class PaymentDto {

    @Builder
    @Getter
    public static class EnrollRequest {
        private String authKey;
        private String customerKey;

        public TossDto.issueBillingKeyRequest toIssueBillingKeyDto() {
            return TossDto.issueBillingKeyRequest.builder()
                    .authKey(this.authKey)
                    .customerKey(this.customerKey)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class EnrollResponse {
        private Long idx;
        private String alias;
    }
}
