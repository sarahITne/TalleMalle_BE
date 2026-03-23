package org.example.tallemalle_backend.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notice API", description = "공지사항 관련(등록·수정·삭제·조회) API")
@RequiredArgsConstructor
@RequestMapping("/notices")
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 작성
    @Operation(summary = "공지사항 작성", description = "관리자 권한을 가진 사용자가 새로운 공지사항을 등록합니다.")
    @PostMapping
    public ResponseEntity createNotice(
            @AuthenticationPrincipal AuthUserDetails user,
            @Valid @RequestBody NoticeDto.CreateReq dto) {
        // 1. 권한 체크: 사용자의 권한 중 ROLE_ADMIN이 있는지 확인
        checkAdminRole(user);

        // 2. 관리자인 경우에만 서비스 호출
        NoticeDto.CreateRes result = noticeService.createNotice(user, dto);
        return ResponseEntity.ok(result);
    }


    // 공지사항 수정
    @Operation(summary = "공지사항 수정", description = "특정 ID의 공지사항 내용을 수정합니다.")
    @PatchMapping("/{idx}")
    public ResponseEntity updateNotice(
            @Parameter(description = "수정할 공지사항 번호", example = "1") @PathVariable Long idx,
            @AuthenticationPrincipal AuthUserDetails user,  // 수정 권한 확인
            @Valid @RequestBody NoticeDto.UpdateReq dto) {
        // 1. 권한 체크: 사용자의 권한 중 ROLE_ADMIN이 있는지 확인
        checkAdminRole(user);

        NoticeDto.DetailRes result = noticeService.updateNotice(idx, user, dto);
        return ResponseEntity.ok(result);
    }


    // 공지사항 삭제
    @Operation(summary = "공지사항 삭제", description = "특정 ID의 공지사항을 삭제합니다.")
    @DeleteMapping("/{idx}")
    public ResponseEntity deleteNotice(
            @Parameter(description = "삭제할 공지사항 번호", example = "1") @PathVariable Long idx,
            @AuthenticationPrincipal AuthUserDetails user) {  // 삭제 권한 확인
        // 1. 권한 체크: 사용자의 권한 중 ROLE_ADMIN이 있는지 확인
        checkAdminRole(user);

        noticeService.deleteNotice(idx, user);
        return ResponseEntity.ok(idx + "번 공지사항이 삭제 완료 되었습니다.");
    }


    // 관리자 권한 체크 (공지사항 작성, 수정, 삭제)
    private void checkAdminRole(AuthUserDetails user) {
        // 권한 체크: 사용자의 권한 중 ROLE_ADMIN이 있는지 확인
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // 관리자가 아닌 경우 BaseException 발생
            throw BaseException.from(BaseResponseStatus.ADMIN_ONLY_ACCESS);
        }
    }
  
  
    // 공지사항 목록 조회 (Slice 페이징 조회)
    @Operation(summary = "공지사항 목록 조회", description = "Slice 방식(무한 스크롤)으로 페이징된 공지사항 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getNotices(
            @ParameterObject // Pageable 객체를 개별 파라미터로 분리
            @PageableDefault(size = 10, sort = "idx", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Slice<NoticeDto.ListRes> result = noticeService.getNotices(pageable);
        return ResponseEntity.ok(result);
    }


    // 공지사항 상세 조회 (단건 조회)
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 번호(idx)를 통해 단건의 상세 정보를 조회합니다.")
    @GetMapping("/{idx}")
    public ResponseEntity getNotice(
            @Parameter(description = "조회할 공지사항 번호", example = "1") @PathVariable Long idx
    ) {
        NoticeDto.DetailRes result = noticeService.getNotice(idx);
        return ResponseEntity.ok(result);
    }
}
