package com.tallemalle.api.notice;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.notice.model.NoticeDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NoticeRepository {
    // 4. DB 서버에 연결하는 클라이언트를 만듦
    private final DataSource ds;

    public NoticeRepository(DataSource ds) {
        this.ds = ds;
    }

    // 5. DB에 CRUD 실행
    public List<NoticeDto.NoticeRes> findAll() {
        String sql = "SELECT * FROM notice";

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            // DB 연결 객체를 다 사용하고 나면 반납할 수 있도록 수정
            try (Connection conn = ds.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();    // DB에서 가져온 결과표 저장

                List<NoticeDto.NoticeRes> list = new ArrayList<>();

                while (rs.next()) {         // 다음 행으로 이동, 처음에는 커서가 아무것도 가리키지 않았는데 이거 실행하자 마자 바로 1번행으로 이동 (이동 가능하면 : true, 다음 행 없어서 이동 못했으면 : false)
                    NoticeDto.NoticeRes returnDto = new NoticeDto.NoticeRes(
                            rs.getLong("notice_idx"),
                            rs.getString("title"),
                            rs.getString("tag"),
                            rs.getBoolean("is_pinned"),
                            rs.getTimestamp("created_at").toLocalDateTime()

                    );
                    list.add(returnDto);
                }
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NoticeDto.NoticeDetailRes findbyId(Long noticeIdx) {
        String sql = "SELECT * FROM notice WHERE notice_idx = ?";

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            // DB 연결 객체를 다 사용하고 나면 반납할 수 있도록 수정
            try (Connection conn = ds.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, noticeIdx);
                ResultSet rs = pstmt.executeQuery();    // DB에서 가져온 결과표 저장

                if (rs.next()) {         // 다음 행으로 이동, 처음에는 커서가 아무것도 가리키지 않았는데 이거 실행하자 마자 바로 1번행으로 이동 (이동 가능하면 : true, 다음 행 없어서 이동 못했으면 : false)
                    return new NoticeDto.NoticeDetailRes(
                            rs.getLong("notice_idx"),
                            rs.getString("title"),
                            rs.getString("contents"),
                            rs.getString("tag"),
                            rs.getBoolean("is_pinned"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
