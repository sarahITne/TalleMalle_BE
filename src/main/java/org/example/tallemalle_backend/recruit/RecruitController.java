package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/recruit")
@RestController
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @PostMapping
    public ResponseEntity reg(@AuthenticationPrincipal AuthUserDetails user, @RequestBody RecruitDto.RegReq dto) {
        recruitService.reg(user, dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    @GetMapping
    public ResponseEntity list() {
        List<RecruitDto.ListRes> result = recruitService.list();
        return ResponseEntity.ok(BaseResponse.success(result));
    }


}
