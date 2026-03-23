package org.example.tallemalle_backend.recruit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.participation.model.ParticipationDto;
import org.example.tallemalle_backend.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitDto {
    // 모집글 등록 Request
    @Schema(name = "RecruitRegReq", description = "모집글 등록 요청 데이터")
    @Getter
    @Builder
    public static class RegReq {
        @Schema(description = "출발지 명칭", example = "강남역 2번 출구")
        private String startPointName;
        @Schema(description = "출발지 위도", example = "37.4979")
        private Double startLat;
        @Schema(description = "출발지 경도", example = "127.0276")
        private Double startLng;
        @Schema(description = "도착지 명칭", example = "판교역 1번 출구")
        private String destPointName;
        @Schema(description = "도착지 위도", example = "37.3947")
        private Double destLat;
        @Schema(description = "도착지 경도", example = "127.1111")
        private Double destLng;
        @Schema(description = "출발 예정 시간", example = "2026-03-20 16:13:00.000000")
        private LocalDateTime departureTime;
        @Schema(description = "최대 탑승 인원 (방장 포함)", example = "4")
        private Integer maxCapacity;
        @Schema(description = "모집글 상세 설명", example = "조용히 가실 분 구합니다~")
        private String description;

        public Recruit toEntity(User user) {
            return Recruit.builder()
                    .owner(user)
                    .description(this.description)
                    .startPointName(this.startPointName)
                    .startLat(this.startLat)
                    .startLng(this.startLng)
                    .destPointName(this.destPointName)
                    .destLat(this.destLat)
                    .destLng(this.destLng)
                    .departureTime(this.departureTime)
                    .maxCapacity(this.maxCapacity)
                    .currentCapacity(1)
                    .status(RecruitStatus.RECRUITING)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    // 모집글 리스트 조회 Response
    @Schema(name = "RecruitListRes", description = "모집글 리스트 조회 응답 데이터")
    @Getter
    @Builder
    public static class ListRes {
        @Schema(description = "모집글 식별자", example = "1")
        private Long idx;
        @Schema(description = "방장 유저 식별자", example = "10")
        private Long ownerId;
        @Schema(description = "현재 참여 중인 유저 목록")
        private List<ParticipationDto.ReadRes> participationList;
        @Schema(description = "모집글 상세 설명", example = "조용히 가실 분 구합니다~")
        private String description;
        @Schema(description = "출발지 명칭", example = "강남역 2번 출구")
        private String startPointName;
        @Schema(description = "출발지 위도", example = "37.4979")
        private Double startLat;
        @Schema(description = "출발지 경도", example = "127.0276")
        private Double startLng;
        @Schema(description = "도착지 명칭", example = "판교역 1번 출구")
        private String destPointName;
        @Schema(description = "도착지 위도", example = "37.3947")
        private Double destLat;
        @Schema(description = "도착지 경도", example = "127.1111")
        private Double destLng;
        @Schema(description = "출발 예정 시간", example = "2026-03-20 16:13:00.000000")
        private LocalDateTime departureTime;
        @Schema(description = "최대 탑승 인원 (방장 포함)", example = "4")
        private Integer maxCapacity;
        @Schema(description = "현재 탑승 인원", example = "2")
        private Integer currentCapacity;
        @Schema(description = "현재 모집 상태", example = "RECRUITING")
        private RecruitStatus status;
        @Schema(description = "모집글 생성 시간")
        private LocalDateTime createdAt;

        public static ListRes from(Recruit entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .ownerId(entity.getOwner().getIdx())
                    .participationList(entity.getParticipations().stream()
                            .filter(p -> p.isActive())
                            .map(ParticipationDto.ReadRes::from)
                            .toList())
                    .description(entity.getDescription())
                    .startPointName(entity.getStartPointName())
                    .startLat(entity.getStartLat())
                    .startLng(entity.getStartLng())
                    .destPointName(entity.getDestPointName())
                    .destLat(entity.getDestLat())
                    .destLng(entity.getDestLng())
                    .departureTime(entity.getDepartureTime())
                    .maxCapacity(entity.getMaxCapacity())
                    .currentCapacity(entity.getCurrentCapacity())
                    .status(entity.getStatus())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
    // 모집글 상세 조회 Response
    @Schema(name = "RecruitDetailRes", description = "모집글 상세 조회 응답 데이터")
    @Builder
    @Getter
    public static class DetailRes {
        @Schema(description = "모집글 식별자", example = "1")
        private Long idx;
        @Schema(description = "출발지 명칭", example = "강남역 2번 출구")
        private String startPointName;
        @Schema(description = "도착지 명칭", example = "판교역 1번 출구")
        private String destPointName;
        @Schema(description = "출발 예정 시간", example = "2026-03-20 16:13:00.000000")
        private LocalDateTime departureTime;
        @Schema(description = "최대 탑승 인원 (방장 포함)", example = "4")
        private Integer maxCapacity;
        @Schema(description = "현재 탑승 인원", example = "2")
        private Integer currentCapacity;
        @Schema(description = "방장 닉네임", example = "김기사")
        private String ownerName;
        @Schema(description = "현재 모집 상태", example = "RECRUITING")
        private RecruitStatus status;
        @Schema(description = "배정된 기사님 이름", example = "홍길동")
        private String driverName;

        public static DetailRes from(Recruit entity) {
            return from(entity, null);
        }

        public static DetailRes from(Recruit entity, String driverName) {
            return DetailRes.builder()
                    .idx(entity.getIdx())
                    .startPointName(entity.getStartPointName())
                    .destPointName(entity.getDestPointName())
                    .departureTime(entity.getDepartureTime())
                    .maxCapacity(entity.getMaxCapacity())
                    .currentCapacity(entity.getCurrentCapacity())
                    .ownerName(entity.getOwner() != null ? entity.getOwner().getName() : null)
                    .status(entity.getStatus())
                    .driverName(driverName)
                    .build();
          }
    }
}
