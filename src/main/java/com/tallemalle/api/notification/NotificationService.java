package com.tallemalle.api.notification;

import com.tallemalle.api.notification.model.NotificationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationDto.Response read(long userId) {

        List<NotificationDto.Response.Item> items = notificationRepository.findAllByUserId(userId);
        long unreadCount = notificationRepository.countUnread(userId);

        return new NotificationDto.Response(unreadCount, items);
    }
    public NotificationDto.Response readUnreadOnly(Long userId){
        List<NotificationDto.Response.Item> items = notificationRepository.findUnreadTop5(userId);

        long unreadCount = notificationRepository.countUnread(userId);

        return new NotificationDto.Response(unreadCount, items);
    }
    public Map<String, Object> readAll(long userId) {
        int count = notificationRepository.updateAllRead(userId);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("updatedCount", count);
        resultData.put("message", "모든 알림이 읽음 처리되었습니다.");

        return resultData;
    }
}
