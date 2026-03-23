package org.example.tallemalle_backend.recruit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@Tag(name = "Recruit API", description = "모집글 관련 API")
@RequestMapping("/recruit")
@RestController
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @Operation(summary = "모집글 생성", description = "새로운 모집글 등록하는 기능")
    @PostMapping
    public ResponseEntity reg(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @RequestBody RecruitDto.RegReq dto
    ) {
        recruitService.reg(user, dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 사용자의 현재 화면의 남서쪽/북동쪽 위경도 좌표 받아서 처리
    @Operation(summary = "모집글 지도 화면 검색", description = "사용자의 현재 화면(남서쪽/북동쪽 좌표) 내에 있는 모집글 리스트를 조회하는 기능")
    @GetMapping("/search")
    public ResponseEntity search(
            @RequestParam Double swLat, @RequestParam Double swLng,
            @RequestParam Double neLat, @RequestParam Double neLng,
            @PageableDefault(size = 20, sort = "departureTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Slice<RecruitDto.ListRes> result = recruitService.search(swLat, swLng, neLat, neLng, pageable);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "모집글 참여", description = "특정 모집글에 참여하는 기능")
    @PostMapping("/join/{recruitIdx}")
    public ResponseEntity join(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "모집글 Idx") @PathVariable Long recruitIdx
    ) {
        boolean result = recruitService.join(user, recruitIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "모집글 퇴장", description = "참여중인 모집글에서 나가는 기능, 방장이 나가는 경우 모집글이 취소된다.")
    @DeleteMapping("/{recruitIdx}/leave")
    public ResponseEntity leave(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails user,
            @Parameter(description = "모집글 Idx") @PathVariable Long recruitIdx
    ) {
        boolean result = recruitService.leave(user, recruitIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 게시글 상세 조회
    @Operation(summary = "모집글 상세 조회", description = "특정 모집글의 상세 정보를 조회하는 기능")
    @GetMapping("/{recruitId}")
    public ResponseEntity detail(@Parameter(description = "모집글 Idx") @PathVariable Long recruitId) {
        RecruitDto.DetailRes dto = recruitService.detail(recruitId);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
}
