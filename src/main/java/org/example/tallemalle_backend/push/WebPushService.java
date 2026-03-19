package org.example.tallemalle_backend.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.push.model.PushSubscription;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushService {
    private final ParticipationRepository participationRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${VAPID_PUBLIC_KEY}")
    private String publicKey;

    @Value("${VAPID_PRIVATE_KEY}")
    private String privateKey;

    @PostConstruct
    public void registerSecurityProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public void notifyRoom(Long recruitId, User sender, String contents) {
        try {
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

            PushService pushService = new PushService(publicKey, privateKey, "mailto:admin@tallemalle.local");
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
        } catch (Exception e) {
            log.warn("푸시 전송 처리 실패: recruitId={}, reason={}", recruitId, e.getMessage());
        }
    }
}
