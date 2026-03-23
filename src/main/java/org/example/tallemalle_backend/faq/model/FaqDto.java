package org.example.tallemalle_backend.faq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "FaqDto", description = "FAQ 관련 DTO 모음")
public class FaqDto {

    /// FAQ 작성 요청 DTO
    @Schema(name = "Faq_CreateReq", description = "FAQ 작성 요청 정보")
    @Getter
    public static class CreateReq {
        @Schema(description = "자주 묻는 질문(질문 내용)", example = "로그인은 어떻게 하나요?", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String question;

        @Schema(description = "질문에 대한 답변", example = "우측 상단 로그인 버튼을 클릭하여 소셜 계정으로 로그인 가능합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
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


    // FAQ 작성, 조회 응답 DTO
    @Schema(name = "Faq_Res", description = "FAQ 응답 정보")
    @Getter
    @Builder
    public static class FaqRes {
        @Schema(description = "FAQ 고유 번호(ID)", example = "1")
        private Long idx;

        @Schema(description = "질문 내용", example = "로그인은 어떻게 하나요?")
        private String question;

        @Schema(description = "답변 내용", example = "우측 상단 로그인 버튼을 클릭하여 소셜 계정으로 로그인 가능합니다.")
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
