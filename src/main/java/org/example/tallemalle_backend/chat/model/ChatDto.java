package org.example.tallemalle_backend.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

import java.util.Date;

public class ChatDto {

    @Getter
    @Setter
    public static class SendReq {
        private String contents;
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
    public static class SendRes {
        private Long idx;
        private String type;
        private String contents;
        private Date createdAt;
        private Long senderId;
        private String senderName;
        private Long writerIdx;
        private String writer;
        private Long recruitIdx;

        public static SendRes from(Chat entity) {
            String messageType = entity.getType() == null ? "message" : entity.getType();
            return SendRes.builder()
                    .idx(entity.getIdx())
                    .type(messageType)
                    .contents(entity.getContents())
                    .senderId(entity.getUser().getIdx())
                    .createdAt(entity.getCreatedAt())
                    .senderName(entity.getUser().getName())
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getName())
                    .recruitIdx(entity.getRecruit().getIdx())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ListRes {
        private Long idx;
        private String type;
        private String contents;
        private Date createdAt;
        private Long senderId;
        private String senderName;
        private Long writerIdx;
        private String writer;
        private Long recruitIdx;

        public static ListRes from(Chat entity) {
            String messageType = entity.getType() == null ? "message" : entity.getType();
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .type(messageType)
                    .contents(entity.getContents())
                    .createdAt(entity.getCreatedAt())
                    .senderId(entity.getUser().getIdx())
                    .senderName(entity.getUser().getName())
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getName())
                    .recruitIdx(entity.getRecruit().getIdx())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class RoomRes {
        private Long recruitIdx;
        private String status;
        private String startPointName;
        private String destPointName;
        private java.time.LocalDateTime departureTime;
        private Integer currentCapacity;
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
