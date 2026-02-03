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
    // 변경: ChatDto.Write.Res -> ChatDto.WriteRes, ChatDto.Write.Req -> ChatDto.WriteReq
    public ChatDto.WriteRes createMessage(ChatDto.WriteReq dto) {
        String sql = "INSERT INTO chat_message (room_id, content, created_at) VALUES (?, ?, NOW())";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, dto.getRoomId());
            pstmt.setString(2, dto.getContent());
            pstmt.executeUpdate();

            // 생성된 메시지 ID 가져오기
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // 변경: new ChatDto.WriteRes
                    return new ChatDto.WriteRes(
                            rs.getInt(1),
                            new Timestamp(System.currentTimeMillis()).toString()
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 저장 중 오류 발생", e);
        }
        return null;
    }

    // 2. 채팅방 목록 조회 (Find All Rooms)
    // 변경: List<ChatDto.RoomList.Res> -> List<ChatDto.RoomListRes>
    public List<ChatDto.RoomListRes> findAllRoomsByUserId(int userId) {
        List<ChatDto.RoomListRes> list = new ArrayList<>();
        String sql = "SELECT r.id, r.departure, r.destination, " +
                "(SELECT content FROM chat_message WHERE room_id = r.id ORDER BY created_at DESC LIMIT 1) as last_msg, " +
                "0 as unread_count " +
                "FROM chat_room r " +
                "JOIN chat_member m ON r.id = m.room_id " +
                "WHERE m.user_id = ?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 변경: new ChatDto.RoomListRes
                    ChatDto.RoomListRes res = new ChatDto.RoomListRes(
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