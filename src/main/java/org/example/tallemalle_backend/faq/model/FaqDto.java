package org.example.tallemalle_backend.faq.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class FaqDto {

    // faq 작성 요청 dto
    @Getter
    public static class CreateReq {
        @NotBlank
        private String title;

        @NotBlank
        private String contents;

        // dto -> 엔티티
        public Faq toEntity() {
            return Faq.builder()
                    .title(this.title)
                    .contents(this.contents)
                    .build();
        }
    }


    // faq 작성 응답 dto
    @Getter
    @Builder
    public static class CreateRes {
        private Long idx;
        private String title;
        private String contents;

        // 엔티티 -> dto
        public static CreateRes from(Faq entity) {
            return CreateRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .build();
        }
    }


    // faq 전체 조회 응답 dto
}
