package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.chat.model.ChatRead;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.push.WebPushService;
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
    private final ChatReadRepository chatReadRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final ParticipationRepository participationRepository;
    private final WebPushService webPushService;

    public ChatDto.SendRes send(AuthUserDetails user, Long recruitIdx, ChatDto.SendReq dto) {
        validateParticipant(user, recruitIdx);
        User chatUser = userRepository.findById(user.getIdx()).orElseThrow();
        Recruit recruit = recruitRepository.findById(recruitIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 recruitId 입니다."));
        Chat entity = Chat.builder()
                .contents(dto.getContents())
                .type(dto.getType() == null ? "message" : dto.getType())
                .recruit(recruit)
                .user(chatUser)
                .build();
        entity = chatRepository.save(entity);

        webPushService.notifyRoom(recruitIdx, chatUser, dto.getContents());
        return ChatDto.SendRes.from(entity);
    }

    public List<ChatDto.ListRes> list(AuthUserDetails user, Long recruitIdx) {
        validateParticipant(user, recruitIdx);
        List<Chat> chatList = chatRepository.findAllByRecruit_IdOrderByIdxAsc(recruitIdx);

        if (!chatList.isEmpty()) {
            Long lastChatIdx = chatList.get(chatList.size() - 1).getIdx();
            upsertReadMarker(user.getIdx(), recruitIdx, lastChatIdx);
        }

        return chatList.stream()
                .map(ChatDto.ListRes::from)
                .toList();
    }

    public List<Long> unreadRecruitIds(AuthUserDetails user) {
        List<Long> recruitIds = participationRepository.findAllByUser_Idx(user.getIdx())
                .stream()
                .map(p -> p.getRecruit().getId())
                .toList();

        if (recruitIds.isEmpty()) {
            return List.of();
        }

        return recruitIds.stream()
                .filter(recruitId -> {
                    Long lastReadIdx = chatReadRepository.findByUser_IdxAndRecruit_Id(user.getIdx(), recruitId)
                            .map(ChatRead::getLastReadChatIdx)
                            .orElse(0L);
                    return chatRepository.existsByRecruit_IdAndIdxGreaterThanAndUser_IdxNot(
                            recruitId,
                            lastReadIdx,
                            user.getIdx()
                    );
                })
                .toList();
    }

    private void validateParticipant(AuthUserDetails user, Long recruitIdx) {
        boolean isParticipant = participationRepository.existsByRecruit_IdAndUser_Idx(recruitIdx, user.getIdx());
        if (!isParticipant) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
        }
    }

    private void upsertReadMarker(Long userIdx, Long recruitIdx, Long lastChatIdx) {
        ChatRead read = chatReadRepository.findByUser_IdxAndRecruit_Id(userIdx, recruitIdx)
                .orElseGet(() -> ChatRead.builder()
                        .user(userRepository.findById(userIdx).orElseThrow())
                        .recruit(recruitRepository.findById(recruitIdx).orElseThrow())
                        .lastReadChatIdx(0L)
                        .build());

        read.setLastReadChatIdx(lastChatIdx);
        chatReadRepository.save(read);
    }
}
