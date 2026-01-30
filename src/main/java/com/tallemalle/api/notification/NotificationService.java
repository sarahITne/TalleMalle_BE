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

    public NotificationDto.NotificationListRes read(long userId) {

        List<NotificationDto.NotificationItemRes> items = notificationRepository.findAll(userId);
        long unreadCount = notificationRepository.countUnread(userId);

        return new NotificationDto.NotificationListRes(unreadCount, items);
    }

    public NotificationDto.NotificationListRes readUnreadOnly(Long userId){
        List<NotificationDto.NotificationItemRes> items = notificationRepository.findUnreadTop5(userId);

        long unreadCount = notificationRepository.countUnread(userId);

        return new NotificationDto.NotificationListRes(unreadCount, items);
    }

    public Map<String, Object> readAll(long userId) {
        int count = notificationRepository.updateAllRead(userId);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("updatedCount", count);
        resultData.put("message", "모든 알림이 읽음 처리되었습니다.");

        return resultData;
    }

    public boolean readOne(long notificationId, long userId) {
        int count = notificationRepository.updateRead(notificationId, userId);

        if (count > 0) {
            return true;
        } else {
            System.out.println(">>> [Service] DB 업데이트 실패 (대상이 없거나 내 알림 아님)");
            return false;
        }
    }
}
