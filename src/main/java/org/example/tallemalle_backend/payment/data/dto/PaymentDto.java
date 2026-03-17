package org.example.tallemalle_backend.payment.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.payment.data.entity.Billing;

import java.util.List;

public class PaymentDto {

    @Builder
    @Getter
    public static class CustomerKeyResponse {
        private String customerKey;
    }

    @Builder
    @Getter
    public static class BillingRes {
        private Long idx;
        private String alias;

        public static BillingRes fromEntity(Billing entity) {
            return BillingRes.builder()
                    .idx(entity.getIdx())
                    .alias(entity.getAlias())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class BillingGroupRes {
        private BillingRes defaultBilling;
        private List<BillingRes> otherBillings;
    }

    @Builder
    @Getter
    public static class EnrollRequest {
        private String authKey;
        private String customerKey;

        public TossDto.IssueBillingKeyRequest toIssueBillingKeyDto() {
            return TossDto.IssueBillingKeyRequest.builder()
                    .authKey(this.authKey)
                    .customerKey(this.customerKey)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class EnrollResponse {
        BillingGroupRes billingGroup;
    }

    @Builder
    @Getter
    public static class RevokeRequest {
        private Long ownerIdx;
        private Long billingIdx;
    }

    @Builder
    @Getter
    public static class RevokeResponse {
        BillingGroupRes billingGroup;
    }

    @Builder
    @Getter
    public static class ListResponse {
        BillingGroupRes billingGroup;
    }

    @Builder
    @Getter
    public static class ChargeRequest {
        private Long recruitIdx;
        private Integer commission;
        private Integer serviceFee;
    }

    @Builder
    @Getter
    public static class ChargeResponse {

    }
}
