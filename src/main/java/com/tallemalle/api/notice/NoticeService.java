package com.tallemalle.api.notice;

import com.tallemalle.api.notice.model.NoticeDto;

import java.util.List;

public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeDto.NoticeRes> getNoticeList() {
        return noticeRepository.findAll();
    }


    // Service 클래스가 필요한 이유 : 비즈니스 로직, 한 기능의 의미와 순서를 정의함 (규칙)
    // 공지사항 상세 조회의 경우 : 조회 수 증가를 해줘야 한다는 규칙
    public NoticeDto.NoticeDetailRes getNoticeDetail(Long noticeIdx) {
        // 조회 수 증가
        noticeRepository.increaseViews(noticeIdx);
        // 상세 조회
        return noticeRepository.findById(noticeIdx);
    }
}
