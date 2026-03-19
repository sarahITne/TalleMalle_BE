package org.example.tallemalle_backend.payment.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.payment.data.TransactionRepository;
import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.payment.data.entity.Transaction;

import java.util.List;

public class PaymentDto {

    @Builder
    @Getter
    public static class CustomerKeyResponse {
        private String customerKey;
    }

    @Builder
    @Getter
    public static class DefaultBillingResponse {

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
    public static class TransactionResponse {
        private Long userIdx;
        private String name;
        private String alias;
        private Integer amount;

        public static TransactionResponse from(Transaction entity) {
            return TransactionResponse.builder()
                    .userIdx(entity.getBilling().getOwner().getIdx())
                    .amount(entity.getOrder().getAmount())
                    .name(entity.getOrder().getName())
                    .alias(entity.getBilling().getAlias())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class TransactionGroupResponse {
        List<TransactionResponse> transactions;
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
        private BillingGroupRes billingGroup;
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
        private BillingGroupRes billingGroup;
    }

    @Builder
    @Getter
    public static class ListResponse {
        private BillingGroupRes billingGroup;
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
        private TransactionGroupResponse transactionGroup;
    }
}
