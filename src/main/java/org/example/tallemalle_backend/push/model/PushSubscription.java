package org.example.tallemalle_backend.push.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.user.model.User;

@Entity
@Table(name = "push_subscriptions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 200)
    private String p256dh;

    @Column(nullable = false, length = 200)
    private String auth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;
}
