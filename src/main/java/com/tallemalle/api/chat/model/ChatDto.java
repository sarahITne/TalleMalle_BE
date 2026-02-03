package com.tallemalle.api.chat.model;

import jakarta.servlet.http.HttpServletRequest;

public class ChatDto {

    // --- 메시지 작성 (Write) ---
    public static class WriteReq {
        private Integer roomId;
        private String content;

        public WriteReq() {}

        public WriteReq(Integer roomId, String content) {
            this.roomId = roomId;
            this.content = content;
        }

        public static WriteReq toDto(HttpServletRequest req) {
            return new WriteReq(
                    req.getParameter("roomId") != null ? Integer.parseInt(req.getParameter("roomId")) : null,
                    req.getParameter("content")
            );
        }

        public Integer getRoomId() { return roomId; }
        public String getContent() { return content; }
    }

    public static class WriteRes {
        private Integer messageId;
        private String createdAt;

        public WriteRes(Integer messageId, String createdAt) {
            this.messageId = messageId;
            this.createdAt = createdAt;
        }

        public Integer getMessageId() { return messageId; }
        public String getCreatedAt() { return createdAt; }
    }


    // --- 채팅방 목록 조회 (RoomList) ---
    public static class RoomListReq {
        private Integer userId;

        public RoomListReq(Integer userId) {
            this.userId = userId;
        }

        public static RoomListReq toDto(HttpServletRequest req) {
            return new RoomListReq(
                    req.getParameter("userId") != null ? Integer.parseInt(req.getParameter("userId")) : null
            );
        }

        public Integer getUserId() { return userId; }
    }

    public static class RoomListRes {
        private Integer roomId;
        private String title;
        private String lastMessage;
        private Integer unreadCount;

        public RoomListRes(Integer roomId, String departure, String destination, String lastMessage, Integer unreadCount) {
            this.roomId = roomId;
            this.title = departure + " → " + destination;
            this.lastMessage = lastMessage;
            this.unreadCount = unreadCount;
        }

        public Integer getRoomId() { return roomId; }
        public String getTitle() { return title; }
        public String getLastMessage() { return lastMessage; }
        public Integer getUnreadCount() { return unreadCount; }
    }


    // --- 사용자 차단 (Block) ---
    public static class BlockReq {
        private Integer targetId;
        private String reason;

        public BlockReq(Integer targetId, String reason) {
            this.targetId = targetId;
            this.reason = reason;
        }

        public static BlockReq toDto(HttpServletRequest req) {
            return new BlockReq(
                    req.getParameter("targetId") != null ? Integer.parseInt(req.getParameter("targetId")) : null,
                    req.getParameter("reason")
            );
        }

        public Integer getTargetId() { return targetId; }
        public String getReason() { return reason; }
    }

    public static class BlockRes {
        private Integer blockId;
        private String blockedAt;

        public BlockRes(Integer blockId, String blockedAt) {
            this.blockId = blockId;
            this.blockedAt = blockedAt;
        }

        public Integer getBlockId() { return blockId; }
        public String getBlockedAt() { return blockedAt; }
    }
}