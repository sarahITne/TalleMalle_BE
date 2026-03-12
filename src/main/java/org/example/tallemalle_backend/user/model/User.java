package org.example.tallemalle_backend.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.common.model.BaseEntity;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private String gender;

    @ColumnDefault(value = "'LOCAL'")
    private String provider;

    @Setter
    @ColumnDefault(value = "'ROLE_USER'")
    private String role;

    @Column(nullable = false)
    @ColumnDefault(value = "'IDLE'")
    private String status;

    @ColumnDefault("false")
    private Boolean enable;
}
