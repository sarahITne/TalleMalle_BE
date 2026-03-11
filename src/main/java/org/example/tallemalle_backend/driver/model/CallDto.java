package org.example.tallemalle_backend.driver.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CallDto {
    // 1. 전체 목록 조회
    @Getter
    @Builder
    public static class ListRes {
        private Long callIdx;
        private String startLocation;
        private CallStatus status;

        public static ListRes from(Call entity){
            return ListRes.builder()
                    .callIdx(entity.getId())
                    .startLocation(entity.getStartLocation())
                    .status(entity.getStatus())
                    .build();
        }
    }

    // 2. 단일 상세 조회
    @Getter
    @Builder
    public static class DetailRes {
        private Long callIdx;
        private Long userIdx;
        private Long driverIdx;
        private String startLocation;
        private String endLocation;
        private CallStatus status;

        public static DetailRes from(Call entity) {
            return DetailRes.builder()
                    .callIdx(entity.getId())
                    .userIdx(entity.getUserIdx())
                    .driverIdx(entity.getDriverIdx())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .status(entity.getStatus())
                    .build();
        }
    }
}
