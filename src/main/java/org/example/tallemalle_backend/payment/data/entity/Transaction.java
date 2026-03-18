package org.example.tallemalle_backend.payment.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String paymentKey;

    @OneToOne
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_idx")
    private Billing billing;
}
