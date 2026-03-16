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

        public Chat toEntity(AuthUserDetails user, Long recruitIdx) {
            return Chat.builder()
                    .contents(this.contents)
                    .recruit(Recruit.builder().id(recruitIdx).build())
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
            return SendRes.builder()
                    .idx(entity.getIdx())
                    .type("message")
                    .contents(entity.getContents())
                    .senderId(entity.getUser().getIdx())
                    .createdAt(entity.getCreatedAt())
                    .senderName(entity.getUser().getName())
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getName())
                    .recruitIdx(entity.getRecruit().getId())
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
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .type("message")
                    .contents(entity.getContents())
                    .createdAt(entity.getCreatedAt())
                    .senderId(entity.getUser().getIdx())
                    .senderName(entity.getUser().getName())
                    .writerIdx(entity.getUser().getIdx())
                    .writer(entity.getUser().getName())
                    .recruitIdx(entity.getRecruit().getId())
                    .build();
        }
    }
}
