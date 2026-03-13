package org.example.tallemalle_backend.driver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userIdx;
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

    @Enumerated(EnumType.STRING)
    private CallStatus status;

    public void accept(Long driverId) {
        this.driverIdx = driverId;
        this.status = CallStatus.ACCEPTED;
    }

    public void cancel() {
        if (this.status == CallStatus.COMPLETED) {
            throw new IllegalStateException("이미 운행이 완료된 콜은 취소할 수 없습니다.");
        }

        this.status = CallStatus.CANCELED;
        this.driverIdx = null;
    }
}
