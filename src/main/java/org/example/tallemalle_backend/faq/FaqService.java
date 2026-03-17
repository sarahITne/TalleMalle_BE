package org.example.tallemalle_backend.faq;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.faq.model.Faq;
import org.example.tallemalle_backend.faq.model.FaqDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    // faq 작성
    public FaqDto.FaqRes createFaq(FaqDto.CreateReq dto) {
        // 1. 요청 DTO를 Entity로 변환하여 저장
        Faq faq = faqRepository.save(dto.toEntity());

        // 2. 저장된 Entity를 응답 DTO로 변환하여 반환
        return FaqDto.FaqRes.from(faq);
    }


    // faq 목록 조회 (전체 조회)
    public List<FaqDto.FaqRes> getFaqs() {
        // 1. 전체 조회 한 결과가 엔티티 타입의 리스트로 반환됨
        List<Faq> faqList = faqRepository.findAll();

        // 2. 조회한 엔티티 리스트를 응답 DTO 타입의 리스트로 바꾸기 위해서 List 생성
        List<FaqDto.FaqRes> result = new ArrayList<>();

        // 3. 엔티티 리스트를 하나씩 DTO로 바꿔가며 응답 DTO 리스트로 변환
        for (Faq faq : faqList) {
            result.add(FaqDto.FaqRes.from(faq));
        }

        // 4. 응답 DTO 리스트 반환
        return result;
    }
}
