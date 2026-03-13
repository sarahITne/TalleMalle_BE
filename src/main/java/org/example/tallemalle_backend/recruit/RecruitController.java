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

    // 모든 모집글 리스트 불러오기
    @GetMapping
    public ResponseEntity list() {
        List<RecruitDto.ListRes> result = recruitService.list();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 사용자의 현재 화면의 남서쪽/북동쪽 위경도 좌표 받아서 처리
    @GetMapping("/search")
    public ResponseEntity search(
            @RequestParam Double swLat, @RequestParam Double swLng,
            @RequestParam Double neLat, @RequestParam Double neLng
    ) {
        List<RecruitDto.ListRes> result = recruitService.search(swLat, swLng, neLat, neLng);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/join/{recruitIdx}")
    public ResponseEntity join(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long recruitIdx) {
        boolean result = recruitService.join(user, recruitIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
