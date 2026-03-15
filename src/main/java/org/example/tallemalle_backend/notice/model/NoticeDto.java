package org.example.tallemalle_backend.notice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

public class NoticeDto {

    // 공지사항 작성 요청
    @Getter
    public static class CreateReq {
        @NotBlank
        private String title;

        @NotBlank
        private String contents;

        // dto -> 엔티티
        public Notice toEntity(AuthUserDetails user) {
            return Notice.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .user(user.toEntity())
                    .build();
        }
    }


    // 공지사항 작성 응답
    @Builder
    @Getter
    public static class CreateRes {
        private Long idx;
        private String title;
        private String contents;
        private String writer;

        // 엔티티 -> dto
        public static CreateRes from(Notice entity) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .writer(entity.getUser().getNickname())
                    .build();
        }
    }
}
