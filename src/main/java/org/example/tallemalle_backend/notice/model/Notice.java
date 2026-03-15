package org.example.tallemalle_backend.notice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseEntity;
import org.example.tallemalle_backend.user.model.User;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Builder.Default
    @Column(nullable = false, length = 55)
    @ColumnDefault(value = "일반")
    private String tag = "일반";

    @Builder.Default
    @Column(nullable = false, name = "is_pinned")
    @ColumnDefault(value = "false")
    private Boolean isPinned = false;

    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer views = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;
}
