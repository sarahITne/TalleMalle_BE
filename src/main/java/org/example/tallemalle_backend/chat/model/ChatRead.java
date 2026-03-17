package org.example.tallemalle_backend.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;

@Entity
@Table(name = "chat_reads")
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
