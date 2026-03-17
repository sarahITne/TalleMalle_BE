package org.example.tallemalle_backend.notice;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notice.model.Notice;
import org.example.tallemalle_backend.notice.model.NoticeDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // 공지사항 수정
    @Transactional
    public NoticeDto.DetailRes updateNotice(Long idx, AuthUserDetails user, NoticeDto.UpdateReq dto) {
        // 1. 게시글 조회 : idx를 통해 수정하고자 하는 공지사항을 찾음, 엔티티 형식으로 반환
        Notice notice = noticeRepository.findById(idx).orElseThrow(
                () -> new IllegalArgumentException("해당 idx의 게시물이 없음")
        );

        // 2. 작성자 검증 (작성자 idx와 현재 로그인한 유저 idx 비교)
        if (!notice.getUser().getIdx().equals(user.getIdx())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 3. 엔티티에 정의해둔 update 메소드 실행, 엔티티 수정 (엔티티 내용을 바꿈 -> 더티체킹)
        notice.update(dto);

        // 4. 수정된 Entity를 응답 DTO로 변환하여 반환
        return NoticeDto.DetailRes.from(notice);
    }


    // 공지사항 삭제
    public void deleteNotice(Long idx, AuthUserDetails user) {
        // 1. 게시글 조회 : idx를 통해 수정하고자 하는 공지사항을 찾음, 엔티티 형식으로 반환
        Notice notice = noticeRepository.findById(idx).orElseThrow(
                () -> new IllegalArgumentException("해당 idx의 게시물이 없음")
        );

        // 2. 작성자 검증 (작성자 idx와 현재 로그인한 유저 idx 비교)
        if (!notice.getUser().getIdx().equals(user.getIdx())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 3. 게시글 삭제
        noticeRepository.deleteById(idx);
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
