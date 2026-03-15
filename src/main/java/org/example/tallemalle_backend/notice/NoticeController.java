package org.example.tallemalle_backend.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/notices")
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity createNotice(
            @AuthenticationPrincipal AuthUserDetails user,
            @Valid @RequestBody NoticeDto.CreateReq dto) {
        NoticeDto.CreateRes result = noticeService.createNotice(user, dto);
        return ResponseEntity.ok(result);
    }
}
