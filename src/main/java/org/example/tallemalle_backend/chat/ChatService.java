package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.chat.model.ChatRead;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
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
        List<Chat> chatList = chatRepository.findAllByRecruit_IdxOrderByIdxAsc(recruitIdx);

        if (!chatList.isEmpty()) {
            Long lastChatIdx = chatList.get(chatList.size() - 1).getIdx();
            upsertReadMarker(user.getIdx(), recruitIdx, lastChatIdx);
        }

        return chatList.stream()
                .map(ChatDto.ListRes::from)
                .toList();
    }

    public List<Long> unreadRecruitIds(AuthUserDetails user) {
        List<Long> recruitIds = participationRepository.findAllByUser_IdxAndStatus(user.getIdx(), ParticipationStatus.ACTIVE)
                .stream()
                .map(p -> p.getRecruit().getIdx())
                .toList();

        if (recruitIds.isEmpty()) {
            return List.of();
        }

        return recruitIds.stream()
                .filter(recruitId -> {
                    Long lastReadIdx = chatReadRepository.findByUser_IdxAndRecruit_Idx(user.getIdx(), recruitId)
                            .map(ChatRead::getLastReadChatIdx)
                            .orElse(0L);
                    return chatRepository.existsByRecruit_IdxAndIdxGreaterThanAndUser_IdxNot(
                            recruitId,
                            lastReadIdx,
                            user.getIdx()
                    );
                })
                .toList();
    }

    public List<ChatDto.RoomRes> rooms(AuthUserDetails user) {
        return participationRepository.findAllByUser_IdxAndStatus(user.getIdx(), ParticipationStatus.ACTIVE)
                .stream()
                .map(p -> ChatDto.RoomRes.from(p.getRecruit()))
                .toList();
    }

    private void validateParticipant(AuthUserDetails user, Long recruitIdx) {
        boolean isParticipant = participationRepository
                .existsByRecruit_IdxAndUser_IdxAndStatus(recruitIdx, user.getIdx(), ParticipationStatus.ACTIVE);
        if (!isParticipant) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
        }
    }

    private void upsertReadMarker(Long userIdx, Long recruitIdx, Long lastChatIdx) {
        ChatRead read = chatReadRepository.findByUser_IdxAndRecruit_Idx(userIdx, recruitIdx)
                .orElseGet(() -> ChatRead.builder()
                        .user(userRepository.findById(userIdx).orElseThrow())
                        .recruit(recruitRepository.findById(recruitIdx).orElseThrow())
                        .lastReadChatIdx(0L)
                        .build());

        read.setLastReadChatIdx(lastChatIdx);
        chatReadRepository.save(read);
    }
}
