package org.example.tallemalle_backend.participation.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.User;

public class ParticipationDto {
    @Builder
    @Getter
    public static class MemberRes {
        private Long userIdx;
        private String userName;

        public static MemberRes from(User user) {
            return MemberRes.builder()
                    .userIdx(user.getIdx())
                    .userName(user.getName())
                    .build();
        }
    }
}
