package org.example.tallemalle_backend.driver.call.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;

import java.util.List;

public class CallDto {
    // 1. 전체 목록 조회
    @Getter
    @Builder
    public static class ListRes {
        private Long callIdx;
        private Long recruitIdx;
        private String startLocation;
        private CallStatus status;

        public static ListRes from(Call entity){
            return ListRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(entity.getRecruit() != null ? entity.getRecruit().getIdx() : null)
                    .startLocation(entity.getStartLocation())
                    .status(entity.getStatus())
                    .build();
        }
    }

    // 2. 운행내역 목록 조회
    @Getter
    @Builder
    public static class HistoryRes {
        private Long callIdx;
        private String startLocation;
        private String endLocation;
        private int estimatedFare;
        private CallStatus status;

        public static HistoryRes from(Call entity) {
            return HistoryRes.builder()
                    .callIdx(entity.getId())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .estimatedFare(entity.getEstimatedFare())
                    .status(entity.getStatus())
                    .build();
        }
    }

    // 3. 단일 상세 조회
    @Getter
    @Builder
    public static class DetailRes {
        private Long callIdx;
        private Long recruitIdx;
        private List<Long> userIdxList;
        private Long driverIdx;
        private String startLocation;
        private String endLocation;
        private Double distance;
        private Integer duration;
        private int estimatedFare;
        private CallStatus status;

        public static DetailRes from(Call entity) {
            return DetailRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(entity.getRecruit() != null ? entity.getRecruit().getIdx() : null)
                    .userIdxList(entity.getRecruit() != null
                            ? entity.getRecruit().getParticipations().stream()
                                .filter(p -> "ACTIVE".equals(p.getStatus()))
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(entity.getDriverIdx())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .status(entity.getStatus())
                    .build();
        }

        public static DetailRes from(Call entity, int estimatedFare) {
            return DetailRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(entity.getRecruit() != null ? entity.getRecruit().getIdx() : null)
                    .userIdxList(entity.getRecruit() != null
                            ? entity.getRecruit().getParticipations().stream()
                                .filter(p -> "ACTIVE".equals(p.getStatus()))
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(entity.getDriverIdx())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .estimatedFare(estimatedFare)
                    .status(entity.getStatus())
                    .build();
        }

        public static DetailRes from(Call call, DirectionInfo direction, int estimatedFare) {
            return DetailRes.builder()
                    .callIdx(call.getId())
                    .recruitIdx(call.getRecruit() != null ? call.getRecruit().getIdx() : null)
                    .userIdxList(call.getRecruit() != null
                            ? call.getRecruit().getParticipations().stream()
                                .filter(p -> "ACTIVE".equals(p.getStatus()))
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(call.getDriverIdx())
                    .startLocation(call.getStartLocation())
                    .endLocation(call.getEndLocation())
                    .status(call.getStatus())
                    .distance(direction.getDistance() / 1000.0) // m -> km
                    .duration(direction.getDuration() / 60)     // 초 -> 분
                    .estimatedFare(estimatedFare)
                    .build();
        }

    }
}
