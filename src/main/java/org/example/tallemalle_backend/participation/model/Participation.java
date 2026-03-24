package org.example.tallemalle_backend.participation.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;

@Entity
@Table(name = "participations")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_idx", nullable = false)
    private Recruit recruit;

    // 참여 상태를 관리하는 컬럼 (예: ACTIVE(참여중), CANCELED(취소), DONE(완료))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipationStatus status;

    public void activate() {
        this.status = ParticipationStatus.ACTIVE;
    }

    public void cancel() {
        this.status = ParticipationStatus.CANCELED;
    }

    public void done() {
        this.status = ParticipationStatus.DONE;
    }

    public boolean isActive() {
        return this.status == ParticipationStatus.ACTIVE;
    }
}