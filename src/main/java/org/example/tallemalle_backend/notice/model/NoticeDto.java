package org.example.tallemalle_backend.notice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.user.model.AuthUserDetails;

import java.util.Date;

@Schema(name = "NoticeDto", description = "공지사항 관련 DTO 모음")
public class NoticeDto {

    // 공지사항 작성 요청
    @Schema(name = "Notice_CreateReq", description = "공지사항 작성 요청 정보")
    @Getter
    public static class CreateReq {
        @Schema(description = "공지사항 제목", example = "서비스 점검 안내", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String title;

        @Schema(description = "공지사항 본문 내용", example = "안녕하세요. 서비스 점검 예정입니다...", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String contents;

        @Schema(description = "카테고리 태그 (공지/업데이트/이벤트/점검/긴급)", example = "공지", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String tag;

        @Schema(description = "상단 고정 여부 (true: 고정, false: 일반)", example = "false")
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
    @Schema(name = "Notice_CreateRes", description = "공지사항 작성 응답 정보")
    @Builder
    @Getter
    public static class CreateRes {
        @Schema(description = "공지사항 고유 번호(ID)", example = "1")
        private Long idx;
        @Schema(description = "작성된 제목", example = "서비스 점검 안내")
        private String title;
        @Schema(description = "작성된 본문", example = "안녕하세요. 서비스 점검 예정입니다...")
        private String contents;
        @Schema(description = "설정된 태그", example = "공지")
        private String tag;
        @Schema(description = "상단 고정 여부", example = "false")
        private Boolean isPinned;
        @Schema(description = "작성자 닉네임", example = "탈래말래 개발팀")
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


    // 공지사항 수정 요청 dto
    @Schema(name = "Notice_UpdateReq", description = "공지사항 수정 요청 정보")
    @Getter
    public static class UpdateReq {
        @Schema(description = "수정할 제목", example = "서비스 점검 시간 변경 안내", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String title;

        @Schema(description = "수정할 본문 내용", example = "점검 시간이 14시에서 15시로 변경되었습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String contents;

        @Schema(description = "수정할 태그", example = "점검", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String tag;

        @Schema(description = "상단 고정 여부 변경", example = "true")
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


    // 공지 사항 목록 조회 응답 dto
    @Schema(name = "Notice_ListRes", description = "공지사항 목록 조회용 간략 정보")
    @Getter
    @Builder
    public static class ListRes {
        @Schema(description = "공지사항 고유 번호", example = "1")
        private Long idx;
        @Schema(description = "제목", example = "서비스 점검 안내")
        private String title;
        @Schema(description = "태그", example = "점검")
        private String tag;
        @Schema(description = "상단 고정 여부", example = "true")
        private Boolean isPinned;
        @Schema(description = "조회수", example = "150")
        private Integer views;
        @Schema(description = "작성자 닉네임", example = "탈래말래 운영팀")
        private String writer;
        @Schema(description = "작성 일시", example = "2026-03-24T02:39:29")
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
    @Schema(name = "Notice_DetailRes", description = "공지사항 상세 응답 정보 (수정 응답 겸용)")
    @Getter
    @Builder
    public static class DetailRes {
        @Schema(description = "공지사항 고유 번호", example = "1")
        private Long idx;
        @Schema(description = "제목", example = "서비스 점검 안내")
        private String title;
        @Schema(description = "본문 내용", example = "안녕하세요. 서비스 점검 예정입니다...")
        private String contents;
        @Schema(description = "태그", example = "공지")
        private String tag;
        @Schema(description = "상단 고정 여부", example = "false")
        private Boolean isPinned;
        @Schema(description = "조회수", example = "155")
        private Integer views;
        @Schema(description = "작성자 닉네임", example = "탈래말래 개발팀")
        private String writer;
        @Schema(description = "작성 일시", example = "2026-03-24T02:39:29")
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

}
