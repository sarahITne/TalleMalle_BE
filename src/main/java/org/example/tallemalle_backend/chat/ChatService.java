package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatDto.SendRes send(ChatDto.SendReq dto) {
        Chat entity = dto.toEntity();
        entity = chatRepository.save(entity);
        return ChatDto.SendRes.from(entity);
    }

    public List<ChatDto.ListRes> list() {
        List<Chat> boardList = chatRepository.findAll();
        return boardList.stream().map(ChatDto.ListRes::from).toList();
    }
}
