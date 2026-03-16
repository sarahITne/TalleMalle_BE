package org.example.tallemalle_backend.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/notices")
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 작성
    @PostMapping
    public ResponseEntity createNotice(
            @AuthenticationPrincipal AuthUserDetails user,
            @Valid @RequestBody NoticeDto.CreateReq dto) {
        NoticeDto.CreateRes result = noticeService.createNotice(user, dto);
        return ResponseEntity.ok(result);
    }


    // 공지사항 목록 조회 (전체 조회)
    @GetMapping
    public ResponseEntity getNotices() {
        List<NoticeDto.ListRes> result = noticeService.getNotices();
        return ResponseEntity.ok(result);
    }


    // 공지사항 상세 조회 (단건 조회)
    @GetMapping("/{idx}")
    public ResponseEntity getNotice(@PathVariable Long idx) {
        NoticeDto.DetailRes result = noticeService.getNotice(idx);
        return ResponseEntity.ok(result);
    }
}
