package org.example.tallemalle_backend.notification;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notification.model.Notification;
import org.example.tallemalle_backend.notification.model.NotificationDto;
import org.example.tallemalle_backend.push.WebPushService;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebPushService webPushService;

    public NotificationDto.PageRes list(Long idx, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("idx").descending());

        Page<Notification> result = notificationRepository.findAllByUserIdx(idx, pageRequest);

        return NotificationDto.PageRes.from(result);
    }

    public List<NotificationDto.ReadTop5Res> summary(AuthUserDetails user) {
        List<Notification> entitiesTop5 = notificationRepository.findTop5ByUserIdxOrderByCreatedAtDesc(user.getIdx());
        return entitiesTop5.stream().map(NotificationDto.ReadTop5Res::from).toList();
    }

    @Transactional
    public NotificationDto.ReadOnlyRes readOnly(AuthUserDetails user, Long idx) {
        Notification entity = notificationRepository.findByUserIdxAndIdx(user.getIdx(), idx).orElseThrow();
        entity.setRead(true);
        Notification result = notificationRepository.save(entity);
        return NotificationDto.ReadOnlyRes.from(result);
    }

    // Refactor : @Modifying 쿼리가 같은 트랜잭션에서 실행되도록 함
    @Transactional
    public void readAll(AuthUserDetails user) {
        notificationRepository.markAllReadByUserIdx(user.getIdx());
    }

    public void createNotification(User user, String type, String title, String contents) {
        createNotification(user, type, title, contents, null);
    }


    // @param recruitIdx 모집(채팅방) ID. {@code matching} 타입이고 값이 있으면 브라우저 Web Push도 전송
    public void createNotification(User user, String type, String title, String contents, Long recruitIdx) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .contents(contents)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "PUSH_NOTIFICATION");
        message.put("title", title);
        message.put("contents", contents);

        simpMessagingTemplate.convertAndSend("/topic/user/" + user.getIdx() + "/notifications", message);

        if ("matching".equals(type) && recruitIdx != null) {
            webPushService.notifyMatching(user.getIdx(), recruitIdx, title, contents);
        }
    }
}
