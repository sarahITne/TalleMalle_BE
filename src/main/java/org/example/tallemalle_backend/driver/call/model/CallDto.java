package org.example.tallemalle_backend.driver.call.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CallDto {
    // 1. 전체 목록 조회
    @Getter
    @Builder
    public static class ListRes {
        private Long callIdx;
        private Long recruitIdx;
        private LocalDateTime departureTime;
        private String startLocation;
        private CallStatus status;

        public static ListRes from(Call entity) {
            Recruit recruit = entity.getRecruit();
            return ListRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(recruit != null ? recruit.getIdx() : null)
                    .departureTime(recruit != null ? recruit.getDepartureTime() : null)
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
        private Long recruitIdx;
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
            /** 총요금 엔빵 시 해당 인원 부담액(나머지 원 단위는 앞쪽 인원에게 배분) */
            private int shareAmount;
        }

        /**
         * @param billableParticipations 정산 대상(CANCELED 제외). 동일 트랜잭션에서 user/profile 지연 로딩 가능.
         * @param fallbackPayer 참가자 행이 없을 때만(예: DB 불일치) 모집 방장 표시용
         */
        public static SettlementRes from(Call call, List<Participation> billableParticipations, User fallbackPayer) {
            int totalFare = call.getEstimatedFare();
            List<Participation> ordered = billableParticipations.stream()
                    .sorted(Comparator.comparing(Participation::getIdx))
                    .toList();
            int n = ordered.size();
            int farePerPerson;
            List<ParticipantInfo> participants;
            if (n > 0) {
                int base = totalFare / n;
                int remainder = totalFare % n;
                farePerPerson = (int) Math.round((double) totalFare / n);
                participants = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    Participation p = ordered.get(i);
                    int share = base + (i < remainder ? 1 : 0);
                    participants.add(ParticipantInfo.builder()
                            .nickname(p.getUser().getNickname())
                            .phoneNumber(p.getUser().getPhoneNumber())
                            .shareAmount(share)
                            .build());
                }
            } else if (fallbackPayer != null) {
                farePerPerson = totalFare;
                participants = List.of(ParticipantInfo.builder()
                        .nickname(fallbackPayer.getNickname())
                        .phoneNumber(fallbackPayer.getPhoneNumber())
                        .shareAmount(totalFare)
                        .build());
            } else {
                farePerPerson = totalFare;
                participants = List.of();
            }

            Recruit linkedRecruit = call.getRecruit();
            return SettlementRes.builder()
                    .callIdx(call.getId())
                    .recruitIdx(linkedRecruit != null ? linkedRecruit.getIdx() : null)
                    .startLocation(call.getStartLocation())
                    .endLocation(call.getEndLocation())
                    .totalFare(totalFare)
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
        private Double startLat;
        private Double startLng;
        private Double endLat;
        private Double endLng;
        private Double distance;
        private Integer duration;
        private int estimatedFare;
        private CallStatus status;

        private static Double coord(BigDecimal v) {
            return v == null ? null : v.doubleValue();
        }

        public static DetailRes from(Call entity) {
            return DetailRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(entity.getRecruit() != null ? entity.getRecruit().getIdx() : null)
                    .userIdxList(entity.getRecruit() != null
                            ? entity.getRecruit().getParticipations().stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(entity.getDriverIdx())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .startLat(coord(entity.getStartLat()))
                    .startLng(coord(entity.getStartLng()))
                    .endLat(coord(entity.getEndLat()))
                    .endLng(coord(entity.getEndLng()))
                    .status(entity.getStatus())
                    .build();
        }

        public static DetailRes from(Call entity, int estimatedFare) {
            return DetailRes.builder()
                    .callIdx(entity.getId())
                    .recruitIdx(entity.getRecruit() != null ? entity.getRecruit().getIdx() : null)
                    .userIdxList(entity.getRecruit() != null
                            ? entity.getRecruit().getParticipations().stream()
                                .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(entity.getDriverIdx())
                    .startLocation(entity.getStartLocation())
                    .endLocation(entity.getEndLocation())
                    .startLat(coord(entity.getStartLat()))
                    .startLng(coord(entity.getStartLng()))
                    .endLat(coord(entity.getEndLat()))
                    .endLng(coord(entity.getEndLng()))
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
                                .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
                                .map(p -> p.getUser().getIdx())
                                .toList()
                            : List.of())
                    .driverIdx(call.getDriverIdx())
                    .startLocation(call.getStartLocation())
                    .endLocation(call.getEndLocation())
                    .startLat(coord(call.getStartLat()))
                    .startLng(coord(call.getStartLng()))
                    .endLat(coord(call.getEndLat()))
                    .endLng(coord(call.getEndLng()))
                    .status(call.getStatus())
                    .distance(direction.getDistance() / 1000.0) // m -> km
                    .duration(direction.getDuration() / 60)     // 초 -> 분
                    .estimatedFare(estimatedFare)
                    .build();
        }

    }
}
