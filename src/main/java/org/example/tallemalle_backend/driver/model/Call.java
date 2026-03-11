package org.example.tallemalle_backend.driver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Long userIdx; // 승객 ID
    private Long driverIdx;    // 기사 ID

    private String startLocation;
    private String endLocation;

    @Enumerated(EnumType.STRING)
    private CallStatus status; // WAITING, ACCEPTED, COMPLETED

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
