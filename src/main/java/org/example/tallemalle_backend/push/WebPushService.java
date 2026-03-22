package org.example.tallemalle_backend.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.profile.ProfileRepository;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.push.model.PushSubscription;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushService {
    private static final String VAPID_MAILTO = "mailto:admin@tallemalle.local";

    private final ParticipationRepository participationRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${webpush.vapid.public-key}")
    private String publicKey;

    @Value("${webpush.vapid.private-key}")
    private String privateKey;

    @PostConstruct
    public void registerSecurityProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }


    // 채팅 메시지 — 방 참여자에게 브라우저 푸시 (발신자 제외)
    public void notifyRoom(Long recruitId, User sender, String contents) {
        try {
            if (!isVapidConfigured()) {
                log.info("웹푸시 비활성화: VAPID 키가 설정되지 않았습니다.");
                return;
            }

            List<Long> userIds = participationRepository.findAllByRecruit_IdxAndStatus(recruitId, ParticipationStatus.ACTIVE)
                    .stream()
                    .map(p -> p.getUser().getIdx())
                    .filter(id -> !id.equals(sender.getIdx()))
                    .toList();

            if (userIds.isEmpty()) {
                return;
            }

            List<PushSubscription> subscriptions = pushSubscriptionRepository.findAllByUser_IdxIn(userIds);
            if (subscriptions.isEmpty()) {
                return;
            }

            String payload = objectMapper.writeValueAsString(Map.of(
                    "title", "새 메시지",
                    "body", sender.getName() + ": " + contents,
                    "recruitId", recruitId
            ));

            sendPayload(subscriptions, payload);
        } catch (Exception e) {
            log.warn("푸시 전송 처리 실패: recruitId={}, reason={}", recruitId, e.getMessage());
        }
    }


     // Refactor : 모집·콜 알림 — 해당 유저의 구독 사이트로 푸시. 채팅과 별도
    public void notifyMatching(Long userIdx, Long recruitId, String title, String body) {
        try {
            if (!isVapidConfigured()) {
                return;
            }
            Optional<Profile> profileOpt = profileRepository.findById(userIdx);
            if (profileOpt.isPresent() && Boolean.FALSE.equals(profileOpt.get().getRecruitPromotionPushEnabled())) {
                return;
            }
            List<PushSubscription> subscriptions = pushSubscriptionRepository.findAllByUser_Idx(userIdx);
            if (subscriptions.isEmpty()) {
                return;
            }
            String payload = objectMapper.writeValueAsString(Map.of(
                    "title", title,
                    "body", body,
                    "recruitId", recruitId
            ));
            sendPayload(subscriptions, payload);
        } catch (Exception e) {
            log.warn("매칭 푸시 전송 실패: userIdx={}, recruitId={}, reason={}", userIdx, recruitId, e.getMessage());
        }
    }

    private boolean isVapidConfigured() {
        return publicKey != null && !publicKey.isBlank() && privateKey != null && !privateKey.isBlank();
    }

    private void sendPayload(List<PushSubscription> subscriptions, String payload) throws Exception {
        PushService pushService = new PushService(publicKey, privateKey, VAPID_MAILTO);
        for (PushSubscription sub : subscriptions) {
            try {
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload
                );
                pushService.send(notification);
            } catch (Exception ex) {
                log.warn("푸시 전송 실패: endpoint={}, reason={}", sub.getEndpoint(), ex.getMessage());
            }
        }
    }
}
