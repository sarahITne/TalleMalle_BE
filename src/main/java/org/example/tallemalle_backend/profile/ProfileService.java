package org.example.tallemalle_backend.profile;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.payment.common.OrderStatus;
import org.example.tallemalle_backend.payment.data.OrderRepository;
import org.example.tallemalle_backend.payment.data.entity.Order;
import org.example.tallemalle_backend.profile.data.dto.ProfileDto;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final OrderRepository orderRepository;

    public ProfileDto.ProfileResponse read(AuthUserDetails user) {
        Profile profile = profileRepository.findById(user.getIdx()).orElseThrow();
        return ProfileDto.ProfileResponse.fromEntity(profile);
    }

    public ProfileDto.ProfileResponse update(AuthUserDetails user, ProfileDto.UpdateRequest dto) {
        Profile profile = profileRepository.findById(user.getIdx()).orElseThrow();
        profile.update(dto.getNickname(), dto.getIntroduction(), dto.getImageUrl());
        profileRepository.save(profile);
        return ProfileDto.ProfileResponse.fromEntity(profile);
    }

    public List<RecruitDto.BoardingRecordRes> history(AuthUserDetails user) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orders = orderRepository.findAllByUserIdxAndStatusWithRecruit(user.getIdx(), OrderStatus.SUCCESS, pageable);

        return orders.getContent().stream()
                .map(RecruitDto.BoardingRecordRes::from)
                .toList();
    }
}
