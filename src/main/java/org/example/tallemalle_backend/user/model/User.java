package org.example.tallemalle_backend.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.common.model.BaseEntity;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.payment.data.entity.Billing;
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

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 10)
    private String gender;

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

    @Setter
    @Builder.Default
    @Column(nullable = false, length = 20)
    @ColumnDefault(value = "'IDLE'")
    private String status = "IDLE";

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

    // 소셜 로그인 후 유저 추가 정보 업데이트
    public void updateExtraInfo(String nickname, String phoneNumber, LocalDate birth, String gender) {
        this.nickname = nickname; // 닉네임을 이름 필드에 저장하거나 별도 필드 사용
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.gender = gender;
        this.role = "ROLE_USER";   // 권한 승격
        this.enable = true;      // 계정 활성화
    }
}