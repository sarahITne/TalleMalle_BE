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

    public NoticeDto.NoticeDetailRes getNoticeDetail(Long noticeIdx) {
        return noticeRepository.findbyId(noticeIdx);
    }
}
