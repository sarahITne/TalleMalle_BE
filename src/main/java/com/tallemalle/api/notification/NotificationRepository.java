package com.tallemalle.api.notification;

import com.tallemalle.api.notification.model.NotificationDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private final DataSource ds;

    public NotificationRepository(DataSource ds) {
        this.ds = ds;
    }

    // 1. 목록 조회
    public List<NotificationDto.Response.Item> findAll(long userId) {
        List<NotificationDto.Response.Item> list = new ArrayList<>();

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY created_at DESC";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, userId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    NotificationDto.Response.Item item = new NotificationDto.Response.Item(
                            rs.getLong("id"),
                            rs.getString("type"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : ""
                    );
                    list.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // 2. 안 읽은 개수 조회
    public long countUnread(long userId) {
        long count = 0;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                String sql = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = 0";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, userId);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    count = rs.getLong(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    // 3. 안읽은 알림 Top5 조회
    public List<NotificationDto.Response.Item> findUnreadTop5(long userId) {
        List<NotificationDto.Response.Item> list = new ArrayList<>();

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                String sql = "SELECT id, type, content, created_at " +
                        "FROM notification " +
                        "WHERE user_id = ? AND is_read = 0 " +
                        "ORDER BY created_at DESC LIMIT 5";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, userId);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    NotificationDto.Response.Item item = new NotificationDto.Response.Item(
                            rs.getLong("id"),
                            rs.getString("type"),
                            null,
                            rs.getString("content"),
                            false,
                            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : ""
                    );
                    list.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // 4. 모두 읽음 처리
    public int updateAllRead(long userId) {
        int affectedRows = 0;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                // user_id가 일치하고, 아직 안 읽은(0) 것만 읽음(1)으로 변경
                String sql = "UPDATE notification SET is_read = 1 " +
                        "WHERE user_id = ? AND is_read = 0";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, userId);

                affectedRows = pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return affectedRows;
    }

    // 5. 개별 알림 읽음 처리
    public int updateRead(long notificationId, long userId) {
        int updatedCount = 0;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                String sql = "UPDATE notification SET is_read = 1 " +
                        "WHERE id = ? AND user_id = ?";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, notificationId);
                pstmt.setLong(2, userId);

                updatedCount = pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return updatedCount;
    }
}