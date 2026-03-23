package org.example.tallemalle_backend.recruit.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruits")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    @Setter
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_point_name", length = 100, nullable = false)
    private String startPointName;

    @Column(name = "start_lat", nullable = false)
    private Double startLat;

    @Column(name = "start_lng", nullable = false)
    private Double startLng;

    @Column(name = "dest_point_name", length = 100, nullable = false)
    private String destPointName;

    @Column(name = "dest_lat", nullable = false)
    private Double destLat;

    @Column(name = "dest_lng", nullable = false)
    private Double destLng;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_capacity", nullable = false)
    @Setter
    private Integer currentCapacity;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(length = 20, nullable = false)
    private RecruitStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 유저 입장 시: 인원 증가 및 FULL 상태 검사
    public void addParticipant() {
        this.currentCapacity++;
        if (this.currentCapacity >= this.maxCapacity) {
            this.status = RecruitStatus.FULL;
        }
    }

    // 유저 퇴장 시: 인원 감소 및 RECRUITING 상태 복구
    public void removeParticipant() {
        if (this.currentCapacity > 1) {
            this.currentCapacity--;
        }
        if (this.status == RecruitStatus.FULL) {
            this.status = RecruitStatus.RECRUITING;
        }
    }

    // 방 폭파 시: 상태를 END(Soft Delete)로 변경
    public void cancelRecruit() {
        this.status = RecruitStatus.END;
    }
}
