package com.tallemalle.api.notification;

import com.tallemalle.api.notification.entity.Notification;
import com.tallemalle.api.notification.model.NotificationDto;
import org.hibernate.Session;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationRepository {

    private final DataSource ds;

    public NotificationRepository(DataSource ds) {
        this.ds = ds;
    }

    // 1. 목록 조회 (JPQL 사용)
    public List<NotificationDto.NotificationItemRes> findAll(Session session, long userId) {
        return session.createQuery(
                        "select n from Notification n where n.userId = :userId order by n.createdAt desc", Notification.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(n -> new NotificationDto.NotificationItemRes(
                        n.getId(), n.getType(), n.getTitle(), n.getContent(), n.isRead(), n.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    // 2. 안 읽은 개수 조회
    public long countUnread(Session session, long userId) {
        return session.createQuery(
                        "select count(n) from Notification n where n.userId = :userId and n.isRead = false", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    // 3. 안읽은 알림 Top5 조회
    public List<NotificationDto.NotificationItemRes> findUnreadTop5(Session session, long userId) {
        return session.createQuery(
                        "select n from Notification n where n.userId = :userId and n.isRead = false order by n.createdAt desc", Notification.class)
                .setParameter("userId", userId)
                .setMaxResults(5) // LIMIT 5
                .getResultList()
                .stream()
                .map(n -> new NotificationDto.NotificationItemRes(
                        n.getId(), n.getType(), null, n.getContent(), n.isRead(), n.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    // 4. 모두 읽음 처리
    public int updateAllRead(Session session, long userId) {

        int result = session.createQuery(
                        "update Notification n set n.isRead = true where n.userId = :userId and n.isRead = false")
                .setParameter("userId", userId)
                .executeUpdate();

        return result;
    }

    // 5. 개별 알림 읽음 처리
    public int updateRead(Session session, long notificationId, long userId) {
        // 객체를 찾아서 영속성 컨텍스트에 올림
        Notification notification = session.find(Notification.class, notificationId);

        if (notification != null && notification.getUserId().equals(userId)) {
            notification.markAsRead(); // 엔티티 상태 변경
            return 1;
        }
        return 0;
    }
}