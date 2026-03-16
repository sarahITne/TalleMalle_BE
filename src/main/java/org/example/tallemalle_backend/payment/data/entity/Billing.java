package org.example.tallemalle_backend.payment.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseEntity;
import org.example.tallemalle_backend.user.model.User;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Billing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String billingKey;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User owner;
}
