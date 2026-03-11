package org.example.tallemalle_backend.chat.model;

import lombok.Builder;
import lombok.Getter;

public class ChatDto {

    @Getter
    public static class SendReq {
        private String contents;

        public Chat toEntity() {
            return Chat.builder()
                    .contents(this.contents)
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

        public static ListRes from(Chat entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .contents(entity.getContents())
                    .build();
        }
    }
}
