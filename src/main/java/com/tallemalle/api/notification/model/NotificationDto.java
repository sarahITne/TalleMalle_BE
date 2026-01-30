package com.tallemalle.api.notification.model;

import java.util.List;

public class NotificationDto {

    public static class NotificationListRes {
        private long unreadCount;
        private List<NotificationItemRes> list; // 바로 아래 정의된 Item을 씀

        public NotificationListRes(long unreadCount, List<NotificationItemRes> list) {
            this.unreadCount = unreadCount;
            this.list = list;
        }
        public long getUnreadCount() { return unreadCount; }
        public List<NotificationItemRes> getList() { return list; }
    }

    public static class NotificationItemRes {
        private Long id;
        private String type;
        private String title;
        private String content;
        private boolean isRead;
        private String createdAt;

        public NotificationItemRes(Long id, String type, String title, String content, boolean isRead, String createdAt) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.content = content;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public boolean isRead() { return isRead; }
        public String getCreatedAt() { return createdAt; }
    }
}