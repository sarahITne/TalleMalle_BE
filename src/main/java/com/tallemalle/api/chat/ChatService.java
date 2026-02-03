package com.tallemalle.api.chat;

import com.tallemalle.api.chat.model.ChatDto;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * 메시지 전송 로직
     */
    public ChatDto.WriteRes sendMessage(ChatDto.WriteReq dto) {
        System.out.println("Service 진입 - roomId: " + dto.getRoomId());
        System.out.println("Service 진입 - content: " + dto.getContent());

        // Repository를 통해 DB에 메시지 저장 후 결과 반환
        // (Repository의 메서드 파라미터와 반환 타입도 수정이 필요할 수 있습니다.)
        ChatDto.WriteRes returnDto = chatRepository.createMessage(dto);

        return returnDto;
    }

    /**
     * 채팅방 참여 목록 조회 로직
     */
    public List<ChatDto.RoomListRes> getRoomList(ChatDto.RoomListReq dto) {
        // 1. Repository에서 해당 사용자의 채팅방 데이터를 가져옴
        List<ChatDto.RoomListRes> rawRooms = chatRepository.findAllRoomsByUserId(dto.getUserId());

        // 2. 가공 로직 (현재는 단순히 전달하는 구조)
        List<ChatDto.RoomListRes> processedRooms = new ArrayList<>();
        for (ChatDto.RoomListRes room : rawRooms) {
            processedRooms.add(room);
        }

        return processedRooms;
    }
}