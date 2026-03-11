package org.example.tallemalle_backend.chat.model;

import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

public class ChatDto {

    @Getter
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
        private String contents;

        public static SendRes from(Chat entity) {
            return SendRes.builder()
                    .idx(entity.getIdx())
                    .contents(entity.getContents())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ListRes {
        private Long idx;
        private String contents;
        private String writer;
        private Long recruitIdx;

        public static ListRes from(Chat entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .contents(entity.getContents())
                    .writer(entity.getUser().getName())
                    .recruitIdx(entity.getRecruit().getId())
                    .build();
        }
    }
}
