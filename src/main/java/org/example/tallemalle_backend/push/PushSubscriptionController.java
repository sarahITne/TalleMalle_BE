package org.example.tallemalle_backend.push;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.push.model.PushSubscription;
import org.example.tallemalle_backend.push.model.PushSubscriptionDto;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;

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
