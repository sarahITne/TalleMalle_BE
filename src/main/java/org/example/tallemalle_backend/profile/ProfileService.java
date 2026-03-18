package org.example.tallemalle_backend.profile;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.profile.data.dto.ProfileDto;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileDto.ProfileResponse read(AuthUserDetails user) {
        Profile profile = profileRepository.findById(user.getIdx()).orElseThrow();
        return ProfileDto.ProfileResponse.fromEntity(profile);
    }

    public void update(ProfileDto.UpdateRequest dto) {
        Profile profile = profileRepository.findById(dto.getIdx()).orElseThrow();
        profile.update(dto.getNickname(), dto.getPhoneNumber(), dto.getIntroduction(), dto.getImageUrl());
        profileRepository.save(profile);
    }
}
