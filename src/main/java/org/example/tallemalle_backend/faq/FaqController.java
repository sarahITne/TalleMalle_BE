package org.example.tallemalle_backend.faq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.faq.model.FaqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FAQ API", description = "FAQ 관련(등록·조회) API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/faqs")
public class FaqController {
    private final FaqService faqService;

    // faq 작성
    @Operation(summary = "FAQ 작성", description = "관리자 권한을 가진 사용자가 새로운 FAQ를 등록합니다.")
    @PostMapping
    public ResponseEntity createFaq(@Valid @RequestBody FaqDto.CreateReq dto) {
        FaqDto.FaqRes result = faqService.createFaq(dto);
        return ResponseEntity.ok(result);
    }

    // faq 목록 조회 (전체 조회)
    @Operation(summary = "FAQ 목록 조회", description = "FAQ 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getFaqs() {
        List<FaqDto.FaqRes> result = faqService.getFaqs();
        return ResponseEntity.ok(result);
    }
}
