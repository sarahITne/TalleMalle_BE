package org.example.tallemalle_backend.push;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.profile.ProfileRepository;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.push.model.PushSubscription;
import org.example.tallemalle_backend.push.model.PushSubscriptionDto;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @GetMapping("/preferences")
    public ResponseEntity<BaseResponse<PushSubscriptionDto.PreferencesRes>> getPreferences(
            @AuthenticationPrincipal AuthUserDetails user) {
        if (user == null) {
            return ResponseEntity.status(401).body(BaseResponse.fail(org.example.tallemalle_backend.common.model.BaseResponseStatus.REQUEST_ERROR));
        }
        boolean enabled = true;
        Optional<Profile> profileOpt = profileRepository.findById(user.getIdx());
        if (profileOpt.isPresent()) {
            enabled = !Boolean.FALSE.equals(profileOpt.get().getRecruitPromotionPushEnabled());
        }
        return ResponseEntity.ok(BaseResponse.success(
                PushSubscriptionDto.PreferencesRes.builder().recruitPromotionPushEnabled(enabled).build()));
    }

    @PatchMapping("/preferences")
    @Transactional
    public ResponseEntity<BaseResponse<PushSubscriptionDto.PreferencesRes>> patchPreferences(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody PushSubscriptionDto.PreferencesReq req) {
        if (user == null) {
            return ResponseEntity.status(401).body(BaseResponse.fail(org.example.tallemalle_backend.common.model.BaseResponseStatus.REQUEST_ERROR));
        }
        Profile profile = profileRepository.findById(user.getIdx())
                .orElseThrow(() -> new IllegalStateException("프로필이 없습니다."));
        if (req.getRecruitPromotionPushEnabled() != null) {
            profile.setRecruitPromotionPushEnabled(req.getRecruitPromotionPushEnabled());
        }
        boolean enabled = !Boolean.FALSE.equals(profile.getRecruitPromotionPushEnabled());
        return ResponseEntity.ok(BaseResponse.success(
                PushSubscriptionDto.PreferencesRes.builder().recruitPromotionPushEnabled(enabled).build()));
    }

    @PostMapping("/subscribe")
    public ResponseEntity subscribe(
            @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody PushSubscriptionDto.SubscribeReq dto
    ) {
        if (user == null) {
            return ResponseEntity.status(401).body(BaseResponse.fail(org.example.tallemalle_backend.common.model.BaseResponseStatus.REQUEST_ERROR));
        }

        User entity = userRepository.findById(user.getIdx()).orElseThrow();

        boolean exists = pushSubscriptionRepository.existsByUser_IdxAndEndpoint(entity.getIdx(), dto.getEndpoint());
        if (!exists) {
            PushSubscription subscription = PushSubscription.builder()
                    .user(entity)
                    .endpoint(dto.getEndpoint())
                    .p256dh(dto.getKeys().getP256dh())
                    .auth(dto.getKeys().getAuth())
                    .build();
            pushSubscriptionRepository.save(subscription);
        }

        return ResponseEntity.ok(BaseResponse.success(true));
    }
}
