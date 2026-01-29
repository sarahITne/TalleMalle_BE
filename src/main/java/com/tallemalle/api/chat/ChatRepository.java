package com.tallemalle.api.chat;

import com.tallemalle.api.chat.model.ChatDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final DataSource ds;

    public ChatRepository(DataSource ds) {
        this.ds = ds;
    }

    // 1. 메시지 저장 (Create Message)
    public ChatDto.Write.Res createMessage(ChatDto.Write.Req dto) {
        String sql = "INSERT INTO chat_message (room_id, content, created_at) VALUES (?, ?, NOW())";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, dto.getRoomId());
                pstmt.setString(2, dto.getContent());
                pstmt.executeUpdate();

                // 생성된 메시지 ID 가져오기
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return new ChatDto.Write.Res(
                                rs.getInt(1),
                                new Timestamp(System.currentTimeMillis()).toString()
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 저장 중 오류 발생", e);
        }
        return null;
    }

    // 2. 채팅방 목록 조회 (Find All Rooms)
    public List<ChatDto.RoomList.Res> findAllRoomsByUserId(int userId) {
        List<ChatDto.RoomList.Res> list = new ArrayList<>();
        // 채팅방 정보와 마지막 메시지, 안 읽은 개수를 가져오는 쿼리 (예시)
        String sql = "SELECT r.id, r.departure, r.destination, " +
                "(SELECT content FROM chat_message WHERE room_id = r.id ORDER BY created_at DESC LIMIT 1) as last_msg, " +
                "0 as unread_count " + // 안 읽은 개수 로직은 프로젝트 상황에 맞춰 추가
                "FROM chat_room r " +
                "JOIN chat_member m ON r.id = m.room_id " +
                "WHERE m.user_id = ?";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = ds.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // DTO 생성자 내부에서 departure와 destination을 합쳐 "출발지 → 도착지" 제목을 만듭니다.
                    ChatDto.RoomList.Res res = new ChatDto.RoomList.Res(
                            rs.getInt("id"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            rs.getString("last_msg"),
                            rs.getInt("unread_count")
                    );
                    list.add(res);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("채팅방 목록 조회 중 오류 발생", e);
        }
        return list;
    }
}