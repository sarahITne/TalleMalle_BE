package com.tallemalle.api.payment.model.entity;

import com.tallemalle.api.payment.model.dto.PaymentIssue;
import com.tallemalle.api.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(length = 20)
    private String alias;

    private String mId;
    private String customerKey;
    private OffsetDateTime authenticatedAt;
    private String method;

    @Column(nullable = false, unique = true)
    private String billingKey;

    private String cardCompany;

    @Embedded
    private Card card;

    @Builder
    public Payment(User user, String alias, String mId, String customerKey,
                   OffsetDateTime authenticatedAt, String method, String billingKey,
                   String cardCompany, Card card) {
        this.user = user;
        this.alias = alias;
        this.mId = mId;
        this.customerKey = customerKey;
        this.authenticatedAt = authenticatedAt;
        this.method = method;
        this.billingKey = billingKey;
        this.cardCompany = cardCompany;
        this.card = card;
    }

    // [핵심] DTO -> Entity 변환 로직
    public static Payment from(User user, PaymentIssue.Response res) {

        // 1. DTO 내부의 Card 객체를 엔티티용 Card 객체(@Embeddable)로 변환
        Card embeddedCard = null;
        if (res.getCard() != null) {
            embeddedCard = Card.builder()
                    .issuerCode(res.getCard().getIssuerCode())
                    .acquirerCode(res.getCard().getAcquirerCode())
                    .number(res.getCard().getNumber())
                    .cardType(res.getCard().getCardType())
                    .ownerType(res.getCard().getOwnerType())
                    .build();
        }

        // 2. 전체 Payment 엔티티 빌드
        return Payment.builder()
                .user(user)
                .alias("") // 초기 별칭은 공란
                .mId(res.getMId())
                .customerKey(res.getCustomerKey())
                .authenticatedAt(res.getAuthenticatedAt()) // LocalDateTime 일치
                .method(res.getMethod())
                .billingKey(res.getBillingKey())
                .cardCompany(res.getCardCompany()) // DTO의 루트 레벨 정보
                .card(embeddedCard) // 위에서 만든 임베디드 객체 주입
                .build();
    }
}