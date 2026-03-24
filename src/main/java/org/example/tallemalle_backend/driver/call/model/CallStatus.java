package org.example.tallemalle_backend.driver.call.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum CallStatus {
    WAITING("대기 중"),
    ACCEPTED("수락됨"),
    DRIVING("운행 중"),
    COMPLETED("운행 완료"),
    CANCELED("취소됨"),
    EXPIRED("만료됨");

    private final String description;
}