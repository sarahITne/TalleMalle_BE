package org.example.tallemalle_backend.profile.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.profile.data.entity.Profile;

import java.time.LocalDate;

public class ProfileDto {

    @Builder
    @Getter
    public static class ProfileResponse {
        private Long idx;
        private String nickname;
        private String phoneNumber;
        private LocalDate birth;
        private String gender;
        private String introduction;
        private String imageUrl;

        public static ProfileResponse fromEntity(Profile profile) {
            return ProfileResponse.builder()
                    .idx(profile.getIdx())
                    .nickname(profile.getNickname())
                    .phoneNumber(profile.getPhoneNumber())
                    .birth(profile.getBirth())
                    .gender(profile.getGender())
                    .introduction(profile.getIntroduction())
                    .imageUrl(profile.getImageUrl())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class UpdateRequest {
        private String nickname;
        private String introduction;
        private String imageUrl;
    }
}
