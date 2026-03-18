package org.example.tallemalle_backend.profile.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tallemalle_backend.user.model.User;

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

    public void update(String nickname, String phoneNumber, String introduction, String imageUrl) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }
}
