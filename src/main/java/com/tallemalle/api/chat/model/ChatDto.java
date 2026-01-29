package com.tallemalle.api.chat.model;

import jakarta.servlet.http.HttpServletRequest;

public class ChatDto {
    // 메시지 작성 기능
    public static class Write {
        // 요청
        public static class Req {
            private Integer roomId;
            private String content;

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
}
