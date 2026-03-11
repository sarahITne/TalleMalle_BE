package org.example.tallemalle_backend.recruit.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitDto {

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
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}
