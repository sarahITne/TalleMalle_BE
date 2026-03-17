package org.example.tallemalle_backend.faq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.tallemalle_backend.faq.model.FaqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/faq")
public class FaqController {
    private final FaqService faqService;

    // faq 작성
    @PostMapping
    public ResponseEntity createFaq(@Valid @RequestBody FaqDto.CreateReq dto) {
        FaqDto.CreateRes result = faqService.createFaq(dto);

        return ResponseEntity.ok(result);
    }

    // faq 목록 조회 (전체 조회)
}
