package org.example.tallemalle_backend.driver.call.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.tallemalle_backend.recruit.model.Recruit;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(
        name = "calls",
        indexes = {
                @Index(name = "idx_calls_status_id", columnList = "status,id"),
                @Index(name = "idx_calls_driver_status", columnList = "driver_idx,status,id")
        }
)
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long driverIdx;

    private String startLocation;
    private String endLocation;

    // 출발지 위도/경도
    @Column(precision = 10, scale = 7)
    private BigDecimal startLat;
    @Column(precision = 10, scale = 7)
    private BigDecimal startLng;

    // 목적지 위도/경도
    @Column(precision = 10, scale = 7)
    private BigDecimal endLat;
    @Column(precision = 10, scale = 7)
    private BigDecimal endLng;

    @Builder.Default
    @Setter
    private int estimatedFare = 0;

    @Builder.Default
    @Setter
    private double estimatedDistance = 0;

    @Builder.Default
    @Setter
    private double estimatedDuration = 0;

    @Enumerated(EnumType.STRING)
    private CallStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_idx")
    private Recruit recruit;

    public void accept(Long driverId) {
        this.driverIdx = driverId;
        this.status = CallStatus.ACCEPTED;
    }

    public void startDriving() {
        if (this.status != CallStatus.ACCEPTED) {
            throw new IllegalStateException("수락된 콜만 운행 시작할 수 있습니다.");
        }
        this.status = CallStatus.DRIVING;
    }

    public void complete() {
        if (this.status != CallStatus.DRIVING) {
            throw new IllegalStateException("운행 중인 콜만 완료 처리할 수 있습니다.");
        }
        this.status = CallStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == CallStatus.COMPLETED) {
            throw new IllegalStateException("이미 운행이 완료된 콜은 취소할 수 없습니다.");
        }

        this.status = CallStatus.CANCELED;
        this.driverIdx = null;
    }

    public void expire() {
        if (this.status == CallStatus.COMPLETED || this.status == CallStatus.DRIVING) {
            throw new IllegalStateException("운행 진행/완료 상태 콜은 만료 처리할 수 없습니다.");
        }
        this.status = CallStatus.EXPIRED;
        this.driverIdx = null;
    }
}
