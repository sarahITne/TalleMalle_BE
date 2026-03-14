package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitRepository recruitRepository;

    @GetMapping("/{recruitId}")
    public ResponseEntity detail(@PathVariable Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 recruitId 입니다."));
        RecruitDto.DetailRes dto = RecruitDto.DetailRes.from(recruit);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
}
