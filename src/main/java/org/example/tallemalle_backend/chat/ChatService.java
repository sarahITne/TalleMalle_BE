package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatDto.SendRes send(AuthUserDetails user, Long recruitIdx, ChatDto.SendReq dto) {
        User chatUser = userRepository.findById(user.getIdx()).orElseThrow();
        Chat entity = Chat.builder()
                .contents(dto.getContents())
                .recruit(dto.toEntity(user, recruitIdx).getRecruit())
                .user(chatUser)
                .build();
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
