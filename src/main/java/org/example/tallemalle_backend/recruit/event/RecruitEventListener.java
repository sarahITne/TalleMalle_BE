package org.example.tallemalle_backend.recruit.event;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notification.NotificationService;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecruitEventListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;
    private final ParticipationRepository participationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitUpdated(RecruitEvents.CreatedEvent event) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "newRecruit");
        message.put("payload", event.getDto());
        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitUpdated(RecruitEvents.UpdatedEvent event) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "updateRecruit");
        message.put("payload", event.getDto());
        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitDeleted(RecruitEvents.DeletedEvent event) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "deleteRecruit");
        message.put("payload", event.getRecruitIdx());
        simpMessagingTemplate.convertAndSend("/topic/all-calls", message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitFull(RecruitEvents.FullEvent event) {
        String notificationContents = event.getStartPointName() + " → " + event.getDestPointName() + " 모집 인원이 다 차 모집이 확정되었습니다!";

        participationRepository.findAllByRecruit_IdxAndStatus(event.getRecruitIdx(), ParticipationStatus.ACTIVE).stream()
                .forEach(p -> notificationService.createNotification(
                        p.getUser(),
                        "matching",
                        "모집 확정",
                        notificationContents,
                        event.getRecruitIdx()
                ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserLeft(RecruitEvents.UserLeftEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "leave");
        payload.put("recruitId", event.getRecruitIdx());
        payload.put("senderId", event.getUserIdx());
        payload.put("senderName", event.getUserName());
        payload.put("contents", event.getUserName() + "님이 나갔습니다.");

        simpMessagingTemplate.convertAndSend("/topic/chat/" + event.getRecruitIdx(), payload);
    }
}
