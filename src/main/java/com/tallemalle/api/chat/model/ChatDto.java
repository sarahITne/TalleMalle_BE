package com.tallemalle.api.chat.model;

import jakarta.servlet.http.HttpServletRequest;

public class ChatDto {
    // 메시지 작성 기능
    public static class Write {
        // 요청
        public static class Req {
            private Integer roomId;
            private String content;

            public Req() {
            }

            public Req(Integer roomId, String content) {
                this.roomId = roomId;
                this.content = content;
            }

            public static Req toDto(HttpServletRequest req) {
                return new Req(
                        req.getParameter("roomId") != null ? Integer.parseInt(req.getParameter("roomId")) : null,
                        req.getParameter("content")
                );
            }
            // Getter
            public Integer getRoomId() {
                return roomId;
            }

            public String getContent() {
                return content;
            }
        }
        // 응답
        public static class Res {
            private Integer messageId;
            private String createdAt;

            public Res(Integer messageId, String createdAt) {
                this.messageId = messageId;
                this.createdAt = createdAt;
            }
        }
    }

    // 채팅방 목록 조회 기능
    public static class RoomList {
        // 요청
        public static class Req {
            private  Integer userId;

            public Req(Integer userId) {
                this.userId = userId;
            }

            public static Req toDto(HttpServletRequest req) {
                return new Req(
                        req.getParameter("userId") != null ? Integer.parseInt(req.getParameter("userId")) : null
                );
            }

            public Integer getUserId() {
                return userId;
            }
        }
        // 응답
        public static class Res {
            private Integer roomId;
            private String title;
            private String lastMessage;
            private Integer unreadCount;

            public Res(Integer roomId, String departure, String destination, String lastMessage, Integer unreadCount) {
                this.roomId = roomId;
                this.title = departure + " → " + destination;
                this.lastMessage = lastMessage;
                this.unreadCount = unreadCount;
            }
            // Getter
            public Integer getRoomId() {
                return roomId;
            }

            public String getTitle() {
                return title;
            }

            public String getLastMessage() {
                return lastMessage;
            }

            public Integer getUnreadCount() {
                return unreadCount;
            }
        }
    }

    // 사용자 차단 기능
    public static class Block {
        // 요청
        public static class Req {
            private Integer targetId;
            private  String reason;

            public Req(Integer targetId, String reason) {
                this.targetId = targetId;
                this.reason = reason;
            }

            public static Req toDto(HttpServletRequest req) {
                return new Req(
                        req.getParameter("targetId") != null ? Integer.parseInt(req.getParameter("targetId")) : null,
                        req.getParameter("reason")
                );
            }

            public Integer getTargetId() {
                return targetId;
            }

            public String getReason() {
                return reason;
            }
        }
        // 응답
        public static class Res {
            private Integer blockId;
            private String blockedAt;

            public Res(Integer blockId, String blockedAt) {
                this.blockId = blockId;
                this.blockedAt = blockedAt;
            }

            public Integer getBlockId() {
                return blockId;
            }

            public String getBlockedAt() {
                return blockedAt;
            }
        }
    }
}
