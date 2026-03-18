package org.example.tallemalle_backend.faq.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class FaqDto {

    // faq 작성 요청 dto
    @Getter
    public static class CreateReq {
        @NotBlank
        private String question;

        @NotBlank
        private String answer;

        // dto -> 엔티티
        public Faq toEntity() {
            return Faq.builder()
                    .question(this.question)
                    .answer(this.answer)
                    .build();
        }
    }


    // faq 작성, 조회 응답 dto
    @Getter
    @Builder
    public static class FaqRes {
        private Long idx;
        private String question;
        private String answer;

        // 엔티티 -> dto
        public static FaqRes from(Faq entity) {
            return FaqRes.builder()
                    .idx(entity.getIdx())
                    .question(entity.getQuestion())
                    .answer(entity.getAnswer())
                    .build();
        }
    }
}
