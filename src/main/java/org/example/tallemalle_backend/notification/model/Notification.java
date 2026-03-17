package org.example.tallemalle_backend.notification.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;
    private String type;
    private String title;
    private String contents;
    @Setter
    private boolean isRead;
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_idx")
    private User user;

}
