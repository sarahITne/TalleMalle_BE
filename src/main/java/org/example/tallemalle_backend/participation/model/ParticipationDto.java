package org.example.tallemalle_backend.participation.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.User;

public class ParticipationDto {
    @Getter
    @Builder
    public static class ReadRes {
        private Long idx;
        private Long userIdx;
        private ParticipationStatus status;

        public static ReadRes from(Participation entity) {
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .userIdx(entity.getUser().getIdx())
                    .status(entity.getStatus())
                    .build();
        }
    }
  
    @Builder
    @Getter
    public static class MemberRes {
        private Long userIdx;
        private String userName;
        private String imageUrl;

        public static MemberRes from(User user) {
            String profileImageUrl = null;
            if (user.getProfile() != null) {
                profileImageUrl = user.getProfile().getImageUrl();
            }
            return MemberRes.builder()
                    .userIdx(user.getIdx())
                    .userName(user.getNickname())
                    .imageUrl(profileImageUrl)
                    .build();
        }
    }
}
