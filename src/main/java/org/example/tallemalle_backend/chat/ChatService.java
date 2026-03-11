package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatDto.SendRes send(AuthUserDetails user, Long recruitIdx, ChatDto.SendReq dto) {
        Chat entity = dto.toEntity(user, recruitIdx);
        entity = chatRepository.save(entity);
        return ChatDto.SendRes.from(entity);
    }

    public List<ChatDto.ListRes> list(AuthUserDetails user, Long recruitIdx) {
        List<Chat> chatList = chatRepository.findAllByRecruit_IdOrderByIdxAsc(recruitIdx);

        return chatList.stream()
                .map(ChatDto.ListRes::from)
                .toList();
    }
}
