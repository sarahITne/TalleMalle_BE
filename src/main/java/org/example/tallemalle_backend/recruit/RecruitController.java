package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/recruit")
@RestController
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @GetMapping()
    public ResponseEntity list() {
        List<RecruitDto.ListRes> result = recruitService.list();
        return ResponseEntity.ok(BaseResponse.success(result));
    }


}
