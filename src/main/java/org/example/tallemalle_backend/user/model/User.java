package org.example.tallemalle_backend.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.recruit.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private boolean enable;

    @Column(nullable = false)
    @ColumnDefault(value = "'ROLE_USER'")
    private String role;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit currentRecruit;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Participation> participations = new ArrayList<>();

}
