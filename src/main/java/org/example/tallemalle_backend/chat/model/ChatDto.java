package org.example.tallemalle_backend.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

import java.util.Date;

public class ChatDto {

    @Schema(name = "ChatSendReq", description = "채팅 메시지 전송 요청 데이터")
    @Getter
    @Setter
    public static class SendReq {
        @Schema(description = "메시지 내용", example = "안녕하세요")
        private String contents;
        @Schema(description = "메시지 타입", example = "message")
        private String type;

        public Chat toEntity(AuthUserDetails user, Long recruitIdx) {
            return Chat.builder()
                    .contents(this.contents)
                    .type(this.type)
                    .recruit(Recruit.builder().idx(recruitIdx).build())
                    .user(user.toEntity())
                    .build();
        }
    }

    @Builder
    @Getter
    @Schema(name = "ChatSendRes", description = "채팅 메시지 전송 응답 데이터")
    public static class SendRes {
        @Schema(description = "메시지 식별자", example = "101")
        private Long idx;
        @Schema(description = "메시지 타입", example = "message")
        private String type;
        @Schema(description = "메시지 내용", example = "안녕하세요")
        private String contents;
        @Schema(description = "생성 시간", example = "2026-03-20 16:13:00")
        private Date createdAt;
        @Schema(description = "보낸 사용자 식별자", example = "10")
        private Long senderId;
        @Schema(description = "보낸 사용자 닉네임", example = "김기사")
        private String senderName;
        @Schema(description = "보낸 사용자 프로필 이미지 URL", example = "https://cdn.example.com/profile/10.png")
        private String senderImg;
        @Schema(description = "작성자 식별자", example = "10")
        private Long writerIdx;
        @Schema(description = "작성자 닉네임", example = "김기사")
        private String writer;
        @Schema(description = "모집글 식별자", example = "1")
        private Long recruitIdx;

        public static SendRes from(Chat entity) {
            String messageType = entity.getType() == null ? "message" : entity.getType();
            String profileImageUrl = null;
            if (entity.getUser() != null && entity.getUser().getProfile() != null) {
                profileImageUrl = entity.getUser().getProfile().getImageUrl();
            }
            return SendRes.builder()
                    .idx(entity.getIdx())
                    .type(messageType)
                    .contents(entity.getContents())
                    .senderId(entity.getUser().getIdx())
                    .createdAt(entity.getCreatedAt())
                    .senderName(entity.getUser().getNickname())
                    .senderImg(profileImageUrl)
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getNickname())
                    .recruitIdx(entity.getRecruit().getIdx())
                    .build();
        }
    }

    @Builder
    @Getter
    @Schema(name = "ChatListRes", description = "채팅 메시지 목록 응답 데이터")
    public static class ListRes {
        @Schema(description = "메시지 식별자", example = "101")
        private Long idx;
        @Schema(description = "메시지 타입", example = "message")
        private String type;
        @Schema(description = "메시지 내용", example = "안녕하세요")
        private String contents;
        @Schema(description = "생성 시간", example = "2026-03-20 16:13:00")
        private Date createdAt;
        @Schema(description = "보낸 사용자 식별자", example = "10")
        private Long senderId;
        @Schema(description = "보낸 사용자 닉네임", example = "김기사")
        private String senderName;
        @Schema(description = "보낸 사용자 프로필 이미지 URL", example = "https://cdn.example.com/profile/10.png")
        private String senderImg;
        @Schema(description = "작성자 식별자", example = "10")
        private Long writerIdx;
        @Schema(description = "작성자 닉네임", example = "김기사")
        private String writer;
        @Schema(description = "모집글 식별자", example = "1")
        private Long recruitIdx;

        public static ListRes from(Chat entity) {
            String messageType = entity.getType() == null ? "message" : entity.getType();
            String profileImageUrl = null;
            if (entity.getUser() != null && entity.getUser().getProfile() != null) {
                profileImageUrl = entity.getUser().getProfile().getImageUrl();
            }
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .type(messageType)
                    .contents(entity.getContents())
                    .createdAt(entity.getCreatedAt())
                    .senderId(entity.getUser().getIdx())
                    .senderName(entity.getUser().getNickname())
                    .senderImg(profileImageUrl)
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getNickname())
                    .recruitIdx(entity.getRecruit().getIdx())
                    .build();
        }
    }

    @Builder
    @Getter
    @Schema(name = "ChatRoomRes", description = "채팅방 목록 응답 데이터")
    public static class RoomRes {
        @Schema(description = "모집글 식별자", example = "1")
        private Long recruitIdx;
        @Schema(description = "모집 상태", example = "RECRUITING")
        private String status;
        @Schema(description = "출발지 명칭", example = "강남역 2번 출구")
        private String startPointName;
        @Schema(description = "도착지 명칭", example = "판교역 1번 출구")
        private String destPointName;
        @Schema(description = "출발 예정 시간", example = "2026-03-20 16:13:00.000000")
        private java.time.LocalDateTime departureTime;
        @Schema(description = "현재 탑승 인원", example = "2")
        private Integer currentCapacity;
        @Schema(description = "최대 탑승 인원 (방장 포함)", example = "4")
        private Integer maxCapacity;

        public static RoomRes from(org.example.tallemalle_backend.recruit.model.Recruit entity) {
            return RoomRes.builder()
                    .recruitIdx(entity.getIdx())
                    .status(entity.getStatus().name())
                    .startPointName(entity.getStartPointName())
                    .destPointName(entity.getDestPointName())
                    .departureTime(entity.getDepartureTime())
                    .currentCapacity(entity.getCurrentCapacity())
                    .maxCapacity(entity.getMaxCapacity())
                    .build();
        }
    }
}
