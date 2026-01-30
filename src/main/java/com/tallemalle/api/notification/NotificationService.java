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

    public NotificationDto.NotificationReadAllRes readAll(long userId) {
        int count = notificationRepository.updateAllRead(userId);

        NotificationDto.NotificationReadAllRes resultData = new NotificationDto.NotificationReadAllRes();
        resultData.setUpdatedCount(count);
        resultData.setMessage("모든 알림이 읽음 처리되었습니다.");

        return resultData;
    }

    public NotificationDto.NotificationReadRes readOne(long notificationId, long userId) {
        int count = notificationRepository.updateRead(notificationId, userId);

        if (count > 0) {
            return new NotificationDto.NotificationReadRes(true, "알림 읽음 처리 완료");
        } else {
            return new NotificationDto.NotificationReadRes(false, "권한이 없거나 존재하지 않는 알림입니다.");
        }
    }
}
