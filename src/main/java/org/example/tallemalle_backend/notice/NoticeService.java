package org.example.tallemalle_backend.notice;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.Notice;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 공지사항 작성
    public NoticeDto.CreateRes createNotice(AuthUserDetails user, NoticeDto.CreateReq dto) {
        // 1. 요청 DTO를 Entity로 변환하여 저장
        Notice notice = noticeRepository.save(dto.toEntity(user));

        // 2. 저장된 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.CreateRes.from(notice);
    }


    // 공지사항 목록 조회 (전체 조회)
    public List<NoticeDto.ListRes> getNotices() {
        // 1. 전체 조회 한 결과가 엔티티 타입의 리스트로 반환됨
        List<Notice> noticeList = noticeRepository.findAll();

        // 2. 조회한 엔티티 리스트를 응답 DTO 타입의 리스트로 바꾸기 위해서 List 생성
        List<NoticeDto.ListRes> result = new ArrayList<>();

        // 3. 엔티티 리스트를 하나씩 DTO로 바궈가며 응답 DTO 리스트로 변환
        for (Notice notice : noticeList) {
            result.add(NoticeDto.ListRes.from(notice));
        }

        // 4. 응답 DTO 리스트 반환
        return result;
    }


    // 공지사항 상세 조회 (단건 조회)
    public NoticeDto.DetailRes getNotice(Long idx) {
        // 1. 게시글 조회 결과를 Entity에 저장
        Notice notice = noticeRepository.findById(idx).orElseThrow();

        // 2. 조회 결과 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.DetailRes.from(notice);
    }

}
