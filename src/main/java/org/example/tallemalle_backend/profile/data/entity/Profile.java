package org.example.tallemalle_backend.profile.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tallemalle_backend.user.model.User;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Profile {
    @Id
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column()
    private String introduction;

    @Column()
    private String imageUrl;

    // 모집,콜 웹푸시 수신
    // false면 notifyMatching만 스킵, 채팅 푸시는 유지
    @Setter
    @Builder.Default
    @Column(name = "recruitPromotionPushEnabled", nullable = false)
    @ColumnDefault("true")
    private Boolean recruitPromotionPushEnabled = true;

    public void update(String nickname, String introduction, String imageUrl) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    public void updateExtraInfo(String nickname, String phoneNumber, LocalDate birth, String gender) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.gender = gender;
    }
}
