package org.example.tallemalle_backend.faq;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.faq.model.Faq;
import org.example.tallemalle_backend.faq.model.FaqDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    // faq 작성
    public FaqDto.CreateRes createFaq(FaqDto.CreateReq dto) {
        // 1. 요청 DTO를 Entity로 변환하여 저장
        Faq faq = faqRepository.save(dto.toEntity());

        // 2. 저장된 Entity를 응답 DTO로 변환하여 반환
        return FaqDto.CreateRes.from(faq);
    }


    // faq 목록 조회 (전체 조회)
}
