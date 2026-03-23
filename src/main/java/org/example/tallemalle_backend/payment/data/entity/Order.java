package org.example.tallemalle_backend.payment.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.payment.common.OrderStatus;
import org.example.tallemalle_backend.user.model.User;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Builder.Default
    private String name = "탈래말래 결제";
    private Integer amount;
    @Builder.Default
    @Setter
    private OrderStatus status = OrderStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_idx")
    private org.example.tallemalle_backend.recruit.model.Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_idx")
    private Billing billing;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Transaction transaction;
}
