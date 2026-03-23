package org.example.tallemalle_backend.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;

@Entity
@Table(
        name = "chat_reads",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_reads_user_recruit", columnNames = {"user_idx", "recruit_idx"})
        },
        indexes = {
                @Index(name = "idx_chat_reads_user_recruit", columnList = "user_idx, recruit_idx")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_idx", nullable = false)
    private Recruit recruit;

    @Setter
    @Column(name = "last_read_chat_idx", nullable = false)
    private Long lastReadChatIdx;
}
