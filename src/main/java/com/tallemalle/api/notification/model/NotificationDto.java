package com.tallemalle.api.notification.model;

import java.util.List;

public class NotificationDto {
    // 요청

    // 응답
    public static class NotificationListRes {
        private long unreadCount;
        private List<NotificationItemRes> list;

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

    public static class NotificationReadAllRes {
        Integer updatedCount;
        Object message;

        public NotificationReadAllRes() {
        }

        public NotificationReadAllRes(Object message, Integer updatedCount) {
            this.message = message;
            this.updatedCount = updatedCount;
        }

        public Integer getUpdatedCount() {
            return updatedCount;
        }

        public void setUpdatedCount(Integer updatedCount) {
            this.updatedCount = updatedCount;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }
    }

    public static class NotificationReadRes {
        private boolean success;
        private String message;

        public NotificationReadRes() {
        }

        public NotificationReadRes(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}