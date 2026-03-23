package org.example.tallemalle_backend.participation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.User;

public class ParticipationDto {
    @Getter
    @Builder
    @Schema(name = "ParticipationReadRes", description = "모집글 참여 정보 응답 데이터")
    public static class ReadRes {
        @Schema(description = "참여 식별자", example = "100")
        private Long idx;
        @Schema(description = "참여 유저 식별자", example = "10")
        private Long userIdx;
        @Schema(description = "참여 상태", example = "ACTIVE")
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
    @Schema(name = "ParticipationMemberRes", description = "모집글 참여자 프로필 응답 데이터")
    public static class MemberRes {
        @Schema(description = "참여 유저 식별자", example = "10")
        private Long userIdx;
        @Schema(description = "참여 유저 닉네임", example = "김기사")
        private String userName;
        @Schema(description = "참여 유저 프로필 이미지 URL", example = "https://cdn.example.com/profile/10.png")
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
