package org.example.tallemalle_backend.driver.auth.model;

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
public class Driver extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
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
    @ColumnDefault(value = "'DRIVER'")
    private String role = "DRIVER";

    @Builder.Default
    @Column(nullable = false, length = 20)
    @ColumnDefault(value = "'IDLE'")
    private String status = "IDLE";

    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean enable = false;
}