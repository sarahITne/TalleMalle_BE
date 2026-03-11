package org.example.tallemalle_backend.recruit.model;

import lombok.Builder;
import lombok.Getter;

public class ParticipationDto {
    @Getter
    @Builder
    public static class ReadRes {
        private Long idx;
        private Long useridx;
        private String status;

        public static ReadRes from(Participation entity) {
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .useridx(entity.getUser().getIdx())
                    .status(entity.getStatus())
                    .build();
        }
    }
}
