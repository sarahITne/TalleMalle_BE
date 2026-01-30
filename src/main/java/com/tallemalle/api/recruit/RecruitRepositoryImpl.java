package com.tallemalle.api.recruit;

import com.tallemalle.api.recruit.model.RecruitDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecruitRepositoryImpl implements RecruitRepository {
    private final DataSource ds;

    public RecruitRepositoryImpl(DataSource dataSource) {
        this.ds = dataSource;
    }

    @Override
    public List<RecruitDto.ListRes> findAll(double minLat, double minLng, double maxLat, double maxLng) {
        // [핵심] 지도 범위(좌표) 내에 있는 택시 팟만 조회하는 SQL
        // start_lat이 minLat ~ maxLat 사이이고, start_lng가 minLng ~ maxLng 사이인 것만 가져옴
        String sql = "SELECT * FROM recruit " +
                "WHERE start_lat BETWEEN ? AND ? " +
                "AND start_lng BETWEEN ? AND ?";

        List<RecruitDto.ListRes> list = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 파라미터 바인딩 (순서 중요: SQL 물음표 순서대로)
            pstmt.setDouble(1, minLat); // 첫 번째 ? (start_lat 최소)
            pstmt.setDouble(2, maxLat); // 두 번째 ? (start_lat 최대)
            pstmt.setDouble(3, minLng); // 세 번째 ? (start_lng 최소)
            pstmt.setDouble(4, maxLng); // 네 번째 ? (start_lng 최대)

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. DB 결과(ResultSet)를 RecruitDto(Entity 역할)로 옮겨 담기
                    RecruitDto dto = new RecruitDto();
                    dto.setId(rs.getLong("id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setDesc(rs.getString("desc"));
                    dto.setMaxCount(rs.getInt("max_count"));
                    dto.setCurrentCount(rs.getInt("current_count"));

                    // 날짜 변환 (Timestamp -> LocalDateTime)
                    if (rs.getTimestamp("departure_time") != null) {
                        dto.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    }
                    if (rs.getTimestamp("created_at") != null) {
                        dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }

                    // 위치 정보
                    dto.setStartLocation(rs.getString("start_location"));
                    dto.setStartLat(rs.getDouble("start_lat"));
                    dto.setStartLng(rs.getDouble("start_lng"));
                    dto.setEndLocation(rs.getString("end_location"));
                    dto.setEndLat(rs.getDouble("end_lat"));
                    dto.setEndLng(rs.getDouble("end_lng"));

                    dto.setStatus(rs.getString("status"));
                    dto.setHostId(rs.getLong("host_id"));

                    // 태그 처리: DB에는 "조용히,짐많음" 문자열로 저장됨 -> List로 변환
                    String tagsStr = rs.getString("tags");
                    if (tagsStr != null && !tagsStr.isEmpty()) {
                        dto.setTags(Arrays.asList(tagsStr.split(",")));
                    } else {
                        dto.setTags(new ArrayList<>());
                    }

                    // 2. 목록 조회용 응답 객체(ListRes)로 변환하여 리스트에 추가
                    // (RecruitDto.java에 ListRes 생성자가 있어야 함)
                    list.add(new RecruitDto.ListRes(dto));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB 조회 실패: " + e.getMessage(), e);
        }

        return list;
    }
}
