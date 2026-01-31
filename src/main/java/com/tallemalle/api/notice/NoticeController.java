package com.tallemalle.api.notice;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.notice.model.NoticeDto;
import com.tallemalle.api.notice.model.NoticeDto.NoticeRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

// 1. 공지사항 컨트롤러 생성 (Controller 구현) -> AppConfig에 컨트롤러 등록
// 2. AppConfig에서 -> 현재 클래스의 객체를 생성하고, Map에다 등록
public class NoticeController implements Controller {
    // 4. DB 서버에 연결하는 클라이언트를 만듦
    private final DataSource ds;

    public NoticeController(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getRequestURI().contains("read") && req.getMethod().equals("GET")) {
            System.out.println("DB에서 1개만 조회하는 코드");
        } else if (req.getRequestURI().contains("notice") && req.getMethod().equals("GET")) {
            System.out.println("DB에서 목록을 조회하는 코드");

//            try {
//                Class.forName("org.mariadb.jdbc.Driver");

                // DB 연결 객체를 다 사용하고 나면 반납할 수 있도록 수정
                try (Connection conn = ds.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement(
                            "SELECT * FROM notice");
                    ResultSet rs = pstmt.executeQuery();    // DB에서 가져온 결과표 저장

                    List<NoticeRes> list = new ArrayList<>();

                    while (rs.next()) {         // 다음 행으로 이동, 처음에는 커서가 아무것도 가리키지 않았는데 이거 실행하자 마자 바로 1번행으로 이동 (이동 가능하면 : true, 다음 행 없어서 이동 못했으면 : false)
                        NoticeRes returnDto = new NoticeRes(
                                rs.getLong("notice_idx"),
                                rs.getString("title"),
                                rs.getString("tag"),
                                rs.getBoolean("is_pinned"),
                                rs.getTimestamp("created_at").toLocalDateTime()

                        );
                        list.add(returnDto);
                    }
                    return BaseResponse.success(list);
//                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
