package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.recruit.RecruitRepository;
import org.example.tallemalle_backend.recruit.model.Recruit;
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
    private final RecruitRepository recruitRepository;
    private final ParticipationRepository participationRepository;

    public ChatDto.SendRes send(AuthUserDetails user, Long recruitIdx, ChatDto.SendReq dto) {
        validateParticipant(user, recruitIdx);
        User chatUser = userRepository.findById(user.getIdx()).orElseThrow();
        Recruit recruit = recruitRepository.findById(recruitIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 recruitId 입니다."));
        Chat entity = Chat.builder()
                .contents(dto.getContents())
                .recruit(recruit)
                .user(chatUser)
                .build();
        entity = chatRepository.save(entity);
        return ChatDto.SendRes.from(entity);
    }

    public List<ChatDto.ListRes> list(AuthUserDetails user, Long recruitIdx) {
        validateParticipant(user, recruitIdx);
        List<Chat> chatList = chatRepository.findAllByRecruit_IdOrderByIdxAsc(recruitIdx);

        return chatList.stream()
                .map(ChatDto.ListRes::from)
                .toList();
    }

    private void validateParticipant(AuthUserDetails user, Long recruitIdx) {
        boolean isParticipant = participationRepository.existsByRecruit_IdAndUser_Idx(recruitIdx, user.getIdx());
        if (!isParticipant) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
        }
    }
}
