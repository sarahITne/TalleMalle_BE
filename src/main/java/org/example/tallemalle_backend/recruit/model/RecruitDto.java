package org.example.tallemalle_backend.recruit.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class RecruitDto {
    @Builder
    @Getter
    public static class DetailRes {
        private Long id;
        private String startPointName;
        private String destPointName;
        private LocalDateTime departureTime;
        private Integer maxCapacity;
        private Integer currentCapacity;
        private String ownerName;

        public static DetailRes from(Recruit entity) {
            return DetailRes.builder()
                    .id(entity.getId())
                    .startPointName(entity.getStartPointName())
                    .destPointName(entity.getDestPointName())
                    .departureTime(entity.getDepartureTime())
                    .maxCapacity(entity.getMaxCapacity())
                    .currentCapacity(entity.getCurrentCapacity())
                    .ownerName(entity.getOwner() != null ? entity.getOwner().getName() : null)
                    .build();
        }
    }
}
