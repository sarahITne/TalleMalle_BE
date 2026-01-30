package com.tallemalle.api.notice;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 1. 공지사항 컨트롤러 생성 (Controller 구현) -> AppConfig에 컨트롤러 등록
// 2. AppConfig에서 -> 현재 클래스의 객체를 생성하고, Map에다 등록
public class NoticeController implements Controller {
    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getRequestURI().contains("read") && req.getMethod().equals("GET")) {
            System.out.println("DB에서 1개만 조회하는 코드");
        } else if (req.getRequestURI().contains("notice") && req.getMethod().equals("GET")) {
            System.out.println("DB에서 목록을 조회하는 코드");
        }
        return null;
    }
}
