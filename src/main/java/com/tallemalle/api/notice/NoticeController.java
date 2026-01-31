package com.tallemalle.api.notice;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// 1. 공지사항 컨트롤러 생성 (Controller 구현) -> AppConfig에 컨트롤러 등록
// 2. AppConfig에서 -> 현재 클래스의 객체를 생성하고, Map에다 등록
public class NoticeController implements Controller {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getRequestURI().contains("read") && req.getMethod().equals("GET")) {
            Long noticeIdx = Long.parseLong(req.getParameter("noticeIdx"));

            return  BaseResponse.success(
                    noticeService.getNoticeDetail(noticeIdx)
            );
        } else if (req.getRequestURI().contains("notice") && req.getMethod().equals("GET")) {
            return BaseResponse.success(
                    noticeService.getNoticeList()
            );
        }
        return null;
    }
}
