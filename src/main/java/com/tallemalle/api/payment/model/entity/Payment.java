package com.tallemalle.api.payment.model.entity;

import com.tallemalle.api.payment.model.PaymentIssue;
import com.tallemalle.api.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime authenticatedAt;
    private String method;
    @Column(nullable = false, unique = true)
    private String billingKey;
    private String cardCompany;
    private String cardNumber;
    @Embedded
    private Card card;
    @Builder
    public Payment(User user, String alias, String mId, String customerKey, LocalDateTime authenticatedAt, String method, String billingKey, String cardCompany, String cardNumber, Card card) {
        this.user = user;
        this.alias = alias;
        this.mId = mId;
        this.customerKey = customerKey;
        this.authenticatedAt = authenticatedAt;
        this.method = method;
        this.billingKey = billingKey;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.card = card;
    }

    public static Payment from(User user, PaymentIssue.Response res) {
        //return new Payment(user, "", res.getMId(), res.getCustomerKey(), res.getAuthenticatedAt(), res.getMethod(), res.getBillingKey(), res.getCardCompany(), res.getCardNumber(), res.getCard());
        return null;
    }
}
