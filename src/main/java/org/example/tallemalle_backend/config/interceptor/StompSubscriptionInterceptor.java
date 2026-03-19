package org.example.tallemalle_backend.config.interceptor;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompSubscriptionInterceptor implements ChannelInterceptor {
    private static final String CHAT_TOPIC_PREFIX = "/topic/chat/";

    private final ParticipationRepository participationRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            String destination = accessor.getDestination();
            Long recruitId = parseRecruitId(destination);
            if (recruitId != null) {
                AuthUserDetails user = resolveUser(accessor);
                boolean isParticipant = participationRepository
                        .existsByRecruit_IdxAndUser_IdxAndStatus(recruitId, user.getIdx(), ParticipationStatus.ACTIVE);
                if (!isParticipant) {
                    throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
                }
            }
        }

        return message;
    }

    private Long parseRecruitId(String destination) {
        if (destination == null || !destination.startsWith(CHAT_TOPIC_PREFIX)) {
            return null;
        }
        String raw = destination.substring(CHAT_TOPIC_PREFIX.length());
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private AuthUserDetails resolveUser(StompHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        if (attributes == null) {
            throw new IllegalStateException("웹소켓 세션이 없습니다.");
        }

        Object authObject = attributes.get("user");
        if (!(authObject instanceof Authentication authentication)) {
            throw new IllegalStateException("웹소켓 인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserDetails user)) {
            throw new IllegalStateException("웹소켓 사용자 정보가 없습니다.");
        }

        return user;
    }
}
