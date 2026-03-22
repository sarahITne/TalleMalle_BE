package org.example.tallemalle_backend.driver.call.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.example.tallemalle_backend.participation.model.Participation;

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

    /** 운행 이력 페이징 + 기사 전체 완료 건 예상 요금 합계 */
    @Getter
    @Builder
    public static class HistoryPageRes {
        private List<HistoryRes> content;
        private long totalElements;
        private int totalPages;
        private int number;
        private int size;
        private boolean first;
        private boolean last;
        private long totalEstimatedFare;
    }

    // 3. 운행 완료 정산 조회
    @Getter
    @Builder
    public static class SettlementRes {
        private Long callIdx;
        private String startLocation;
        private String endLocation;
        private int totalFare;
        private int farePerPerson;
        private List<ParticipantInfo> participants;

        @Getter
        @Builder
        public static class ParticipantInfo {
            private String nickname;
            private String phoneNumber;
        }

        public static SettlementRes from(Call call) {
            List<Participation> active = call.getRecruit() != null
                    ? call.getRecruit().getParticipations().stream()
                            .filter(p -> "ACTIVE".equals(p.getStatus()) || "CANCELED".equals(p.getStatus()))
                            .toList()
                    : List.of();

            int count = active.isEmpty() ? 1 : active.size();
            int farePerPerson = call.getEstimatedFare() / count;

            List<ParticipantInfo> participants = active.stream()
                    .map(p -> ParticipantInfo.builder()
                            .nickname(p.getUser().getNickname())
                            .phoneNumber(p.getUser().getPhoneNumber())
                            .build())
                    .toList();

            return SettlementRes.builder()
                    .callIdx(call.getId())
                    .startLocation(call.getStartLocation())
                    .endLocation(call.getEndLocation())
                    .totalFare(call.getEstimatedFare())
                    .farePerPerson(farePerPerson)
                    .participants(participants)
                    .build();
        }
    }

    // 4. 단일 상세 조회
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
                    .distance(entity.getEstimatedDistance())
                    .duration((int) entity.getEstimatedDuration())
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
