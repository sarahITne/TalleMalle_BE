package com.tallemalle.api.chat;

import com.tallemalle.api.chat.model.ChatDto;
import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.utils.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ChatController implements Controller {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        Object result = null;
        String uri = req.getRequestURI();
        String method = req.getMethod();

        // 1. 메시지 작성 기능 (POST /chat/messages)
        if (uri.contains("message") && method.equals("POST")) {
            // JsonParser를 사용하여 요청 데이터를 ChatDto.Write.Req 객체로 변환
            ChatDto.Write.Req dto = JsonParser.from(req, ChatDto.Write.Req.class);
            result = chatService.sendMessage(dto);
        }

        // 2. 채팅방 참여 목록 조회 기능 (GET /chat/rooms)
        else if (uri.contains("rooms") && method.equals("GET")) {
            // 요청 파라미터에서 사용자 ID를 추출하여 DTO 생성 (toDto 메서드 활용)
            ChatDto.RoomList.Req dto = ChatDto.RoomList.Req.toDto(req);
            // 여러 개의 채팅방 목록을 반환하므로 List 형태로 결과 받음
            result = chatService.getRoomList(dto);
        }

        return BaseResponse.success(result);
    }
}