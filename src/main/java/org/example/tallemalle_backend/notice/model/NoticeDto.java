package org.example.tallemalle_backend.notice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

import java.util.Date;

public class NoticeDto {

    // 공지사항 작성 요청 dto
    @Getter
    public static class CreateReq {
        @NotBlank
        private String title;

        @NotBlank
        private String contents;

        @NotBlank
        private String tag;

        private Boolean isPinned;


        // dto -> 엔티티
        public Notice toEntity(AuthUserDetails user) {
            return Notice.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .tag(this.tag)
                    .isPinned(this.isPinned)
                    .user(user.toEntity())
                    .build();
        }
    }


    // 공지사항 작성 응답 dto
    @Builder
    @Getter
    public static class CreateRes {
        private Long idx;
        private String title;
        private String contents;
        private String tag;
        private Boolean isPinned;
        private String writer;

        // 엔티티 -> dto
        public static CreateRes from(Notice entity) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .tag(entity.getTag())
                    .isPinned(entity.getIsPinned())
                    .writer(entity.getUser().getNickname())
                    .build();
        }
    }


    // 공지 사항 목록 조회 응답 dto
    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private String title;
        private String tag;
        private Boolean isPinned;
        private Integer views;
        private String writer;
        private Date createdAt;

        // 엔티티 -> dto
        public static ListRes from(Notice entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .tag(entity.getTag())
                    .isPinned(entity.getIsPinned())
                    .views(entity.getViews())
                    .writer(entity.getUser().getNickname())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }


    // 공지사항 상세 조회, 수정 응답 dto
    @Getter
    @Builder
    public static class DetailRes {
        private Long idx;
        private String title;
        private String contents;
        private String tag;
        private Boolean isPinned;
        private Integer views;
        private String writer;
        private Date createdAt;

        // 엔티티 -> dto
        public static DetailRes from(Notice entity) {
            return DetailRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .tag(entity.getTag())
                    .isPinned(entity.getIsPinned())
                    .views(entity.getViews())
                    .writer(entity.getUser().getNickname())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }


    // 공지사항 수정 요청 dto
    @Getter
    public static class UpdateReq {
        @NotBlank
        private String title;

        @NotBlank
        private String contents;

        @NotBlank
        private String tag;

        private Boolean isPinned;


        // dto -> 엔티티
        public Notice toEntity(AuthUserDetails user) {
            return Notice.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .tag(this.tag)
                    .isPinned(this.isPinned)
                    .user(user.toEntity())
                    .build();
        }
    }
}
