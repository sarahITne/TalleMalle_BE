package org.example.tallemalle_backend.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.common.model.BaseEntity;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false, length = 20)
    @ColumnDefault(value = "'LOCAL'")
    private String provider = "LOCAL";
  
    @Setter
    @Builder.Default
    @Column(nullable = false, length = 20)
    @ColumnDefault(value = "'ROLE_USER'")
    private String role = "ROLE_USER";

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Participation> participations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.IDLE;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean enable = false;

    @Builder.Default
    private String customerKey = UUID.randomUUID().toString();
  
    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "defaultBillingIdx", referencedColumnName = "idx")
    private Billing defaultBilling;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter
    private Profile profile;

    // Profile 필드에 대한 Wrapper Getters (기존 시스템 호환성 유지)
    public String getNickname() {
        return profile != null ? profile.getNickname() : null;
    }

    public String getPhoneNumber() {
        return profile != null ? profile.getPhoneNumber() : null;
    }

    public LocalDate getBirth() {
        return profile != null ? profile.getBirth() : null;
    }

    public String getGender() {
        return profile != null ? profile.getGender() : null;
    }

    // 소셜 로그인 후 유저 추가 정보 업데이트
    public void updateExtraInfo(String nickname, String phoneNumber, LocalDate birth, String gender) {
        if (this.profile == null) {
            this.profile = Profile.builder()
                    .user(this)
                    .nickname(nickname)
                    .phoneNumber(phoneNumber)
                    .birth(birth)
                    .gender(gender)
                    .build();
        } else {
            this.profile.updateExtraInfo(nickname, phoneNumber, birth, gender);
        }
        this.role = "ROLE_USER";   // 권한 승격
        this.enable = true;      // 계정 활성화
    }

    public void changeToOwner() {
        this.status = UserStatus.OWNER;
    }

    public void changeToJoined() {
        this.status = UserStatus.JOINED;
    }

    public void changeToIdle() {
        this.status = UserStatus.IDLE;
    }
}