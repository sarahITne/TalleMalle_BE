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
    public ChatDto.Write.Res sendMessage(ChatDto.Write.Req dto) {
        System.out.println("Service 진입 - roomId: " + dto.getRoomId()); // 1단계 확인
        System.out.println("Service 진입 - content: " + dto.getContent());
        // Repository를 통해 DB에 메시지 저장 후 결과 반환
        ChatDto.Write.Res returnDto = chatRepository.createMessage(dto);

        return returnDto;
    }

    /**
     * 채팅방 참여 목록 조회 로직 (제목 가공 포함)
     */
    public List<ChatDto.RoomList.Res> getRoomList(ChatDto.RoomList.Req dto) {
        // 1. Repository에서 해당 사용자의 채팅방 원본 데이터(Entity 등)를 가져옴
        List<ChatDto.RoomList.Res> rawRooms = chatRepository.findAllRoomsByUserId(dto.getUserId());

        // 2. '출발지 -> 도착지' 형식 등으로 데이터를 가공하여 새 리스트 생성
        // (이미 DTO 생성자에서 가공 로직이 있다면 그대로 사용하고,
        // 필요에 따라 여기서 추가 비즈니스 로직을 수행합니다.)
        List<ChatDto.RoomList.Res> processedRooms = new ArrayList<>();
        for (ChatDto.RoomList.Res room : rawRooms) {
            // 필요 시 여기서 추가적인 필터링이나 데이터 가공 수행
            processedRooms.add(room);
        }

        return processedRooms;
    }
}