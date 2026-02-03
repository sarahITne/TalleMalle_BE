package com.tallemalle.api.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {
    private String issuerCode;
    private String acquirerCode;
    private String number;
    private String cardType;
    private String ownerType;

    @Builder
    public Card(String issuerCode, String acquirerCode, String number, String cardType, String ownerType) {
        this.issuerCode = issuerCode;
        this.acquirerCode = acquirerCode;
        this.number = number;
        this.cardType = cardType;
        this.ownerType = ownerType;
    }
}
