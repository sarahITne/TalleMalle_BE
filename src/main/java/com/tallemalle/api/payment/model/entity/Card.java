package com.tallemalle.api.payment.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Card {
    private String issuerCode;
    private String acquirerCode;
    private String number;
    private String cardType;
    private String ownerType;
}