package org.example.tallemalle_backend.push;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Push Subscription API", description = "웹 푸시 구독 및 알림 설정 API")
@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Operation(summary = "푸시 설정 조회", description = "로그인 사용자의 모집 홍보 푸시 수신 설정을 조회하는 기능")
    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user) {
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

    @Operation(summary = "푸시 설정 수정", description = "로그인 사용자의 모집 홍보 푸시 수신 설정을 수정하는 기능")
    @PatchMapping("/preferences")
    @Transactional
    public ResponseEntity<?> patchPreferences(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
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

    @Operation(summary = "푸시 구독 등록", description = "브라우저 푸시 구독 정보를 서버에 저장하는 기능")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
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
