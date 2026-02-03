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
            // 변경: ChatDto.Write.Req -> ChatDto.WriteReq
            ChatDto.WriteReq dto = JsonParser.from(req, ChatDto.WriteReq.class);
            result = chatService.sendMessage(dto);
        }

        // 2. 채팅방 참여 목록 조회 기능 (GET /chat/rooms)
        else if (uri.contains("rooms") && method.equals("GET")) {
            // 변경: ChatDto.RoomList.Req -> ChatDto.RoomListReq
            ChatDto.RoomListReq dto = ChatDto.RoomListReq.toDto(req);
            result = chatService.getRoomList(dto);
        }

        return BaseResponse.success(result);
    }
}