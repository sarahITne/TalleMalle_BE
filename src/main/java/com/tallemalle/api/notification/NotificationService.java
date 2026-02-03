package com.tallemalle.api.notification;

import com.tallemalle.api.common.DataSourceConfig;
import com.tallemalle.api.notification.model.NotificationDto;
import org.hibernate.Transaction;
import org.hibernate.Session;

import java.util.List;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationDto.NotificationReadRes readOne(long notificationId, long userId) {
        Transaction tx = null;
        try (Session session = DataSourceConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int count = notificationRepository.updateRead(session, notificationId, userId);

            tx.commit();

            if (count > 0) {
                return new NotificationDto.NotificationReadRes(true, "읽음 처리 성공");
            }
            return new NotificationDto.NotificationReadRes(false, "권한이 없거나 존재하지 않는 알림");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    // 2. 안 읽은 알림 요약 조회
    public NotificationDto.NotificationListRes readUnreadOnly(long userId) {
        try (Session session = DataSourceConfig.getSessionFactory().openSession()) {
            // Top 5개만 가져오는 리포지토리 메서드 호출
            List<NotificationDto.NotificationItemRes> items = notificationRepository.findUnreadTop5(session, userId);
            long unreadCount = notificationRepository.countUnread(session, userId);

            return new NotificationDto.NotificationListRes(unreadCount, items);
        }
    }

    public NotificationDto.NotificationReadAllRes readAll(long userId) {
        Transaction tx = null;
        try (Session session = DataSourceConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int count = notificationRepository.updateAllRead(session, userId);

            tx.commit();
            return new NotificationDto.NotificationReadAllRes("모두 읽음 처리 완료", count);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    // 조회(SELECT)의 경우 commit이 필수는 아니지만, 일관성을 위해 세션 관리는 필요합니다.
    public NotificationDto.NotificationListRes read(long userId) {
        try (Session session = DataSourceConfig.getSessionFactory().openSession()) {
            List<NotificationDto.NotificationItemRes> items = notificationRepository.findAll(session, userId);
            long unreadCount = notificationRepository.countUnread(session, userId);

            return new NotificationDto.NotificationListRes(unreadCount, items);
        }
    }
}
