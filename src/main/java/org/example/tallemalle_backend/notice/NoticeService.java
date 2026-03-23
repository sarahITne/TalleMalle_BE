package org.example.tallemalle_backend.notice;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.Notice;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // 공지사항 수정
    @Transactional
    public NoticeDto.DetailRes updateNotice(Long idx, AuthUserDetails user, NoticeDto.UpdateReq dto) {
        // 1. 게시글 조회 : idx를 통해 수정하고자 하는 공지사항을 찾음, 엔티티 형식으로 반환
        Notice notice = noticeRepository.findById(idx).orElseThrow(
                () -> new IllegalArgumentException("해당 idx의 게시물이 없음")
        );

        // 2. 엔티티에 정의해둔 update 메소드 실행, 엔티티 수정 (엔티티 내용을 바꿈 -> 더티체킹)
        notice.update(dto);

        // 3. 수정된 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.DetailRes.from(notice);
    }


    // 공지사항 삭제
    public void deleteNotice(Long idx, AuthUserDetails user) {
        // 1. 게시글 조회 : idx를 통해 수정하고자 하는 공지사항을 찾음, 엔티티 형식으로 반환
        Notice notice = noticeRepository.findById(idx).orElseThrow(
                () -> new IllegalArgumentException("해당 idx의 게시물이 없음")
        );

        // 2. 게시글 삭제
        noticeRepository.deleteById(idx);
    }

  
    // 공지사항 목록 조회 (Slice 페이징 조회)
    @Transactional(readOnly = true)
    public Slice<NoticeDto.ListRes> getNotices(Pageable pageable) {
        // 1. Fetch Join을 사용하여 유저와 프로필 정보를 Slice 형태로 조회 (N+1 해결)
        Slice<Notice> noticeSlice = noticeRepository.findAllSliceBy(pageable);

        // 2. 엔티티 Slice를 응답 DTO Slice로 변환하여 반환
        return noticeSlice.map(NoticeDto.ListRes::from);
    }


    // 공지사항 상세 조회 (단건 조회)
    @Transactional(readOnly = true)
    public NoticeDto.DetailRes getNotice(Long idx) {
        // 1. 게시글 조회 결과를 Entity에 저장
        Notice notice = noticeRepository.findById(idx).orElseThrow(
                () -> new IllegalArgumentException("해당 idx의 게시물이 없음")
        );

        // 2. 조회 결과 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.DetailRes.from(notice);
    }

}
