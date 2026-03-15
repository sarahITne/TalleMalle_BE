package org.example.tallemalle_backend.notice;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.Notice;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 게시글 작성
    public NoticeDto.CreateRes createNotice(AuthUserDetails user, NoticeDto.CreateReq dto) {
        // 1. 요청 DTO를 Entity로 변환하여 저장
        Notice notice = noticeRepository.save(dto.toEntity(user));

        // 2. 저장된 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.CreateRes.from(notice);
    }

}
