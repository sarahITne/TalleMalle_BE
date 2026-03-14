package org.example.tallemalle_backend.recruit.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.user.model.User;
import org.hibernate.annotations.ColumnDefault;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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
    private Integer currentCapacity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
