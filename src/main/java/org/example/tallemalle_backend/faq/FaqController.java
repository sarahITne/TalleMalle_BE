package org.example.tallemalle_backend.faq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.faq.model.FaqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/faqs")
public class FaqController {
    private final FaqService faqService;

    // faq 작성
    @PostMapping
    public ResponseEntity createFaq(@Valid @RequestBody FaqDto.CreateReq dto) {
        FaqDto.FaqRes result = faqService.createFaq(dto);
        return ResponseEntity.ok(result);
    }

    // faq 목록 조회 (전체 조회)
    @GetMapping
    public ResponseEntity getFaqs() {
        List<FaqDto.FaqRes> result = faqService.getFaqs();
        return ResponseEntity.ok(result);
    }
}
