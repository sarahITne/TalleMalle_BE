package org.example.tallemalle_backend.recruit.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.participation.model.ParticipationDto;
import org.example.tallemalle_backend.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitDto {
    // 모집글 등록 Request
    @Getter
    @Builder
    public static class RegReq {
        private String startPointName;
        private Double startLat;
        private Double startLng;
        private String destPointName;
        private Double destLat;
        private Double destLng;
        private LocalDateTime departureTime;
        private Integer maxCapacity;
        private List<String> tags;
        private String description;

        public Recruit toEntity(User user) {
            LocalDateTime parsedDepartureTime = LocalDateTime.now();

            return Recruit.builder()
                    .owner(user) // 방장 설정
                    .description(this.description)
                    .startPointName(this.startPointName)
                    .startLat(this.startLat)
                    .startLng(this.startLng)
                    .destPointName(this.destPointName)
                    .destLat(this.destLat)
                    .destLng(this.destLng)
                    .departureTime(parsedDepartureTime)
                    .maxCapacity(this.maxCapacity)
                    .currentCapacity(1)
                    .status(RecruitStatus.RECRUITING)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    // 모집글 리스트 조회 Response
    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private Long ownerId;
        private List<ParticipationDto.ReadRes> participationList;
        private String description;
        private String startPointName;
        private Double startLat;
        private Double startLng;
        private String destPointName;
        private Double destLat;
        private Double destLng;
        private LocalDateTime departureTime;
        private Integer maxCapacity;
        private Integer currentCapacity;
        private RecruitStatus status;
        private LocalDateTime createdAt;

        public static ListRes from(Recruit entity) {
            return ListRes.builder()
                    .idx(entity.getId())
                    .ownerId(entity.getOwner().getIdx())
                    .participationList(entity.getParticipations().stream().map(ParticipationDto.ReadRes::from).toList())
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
}
