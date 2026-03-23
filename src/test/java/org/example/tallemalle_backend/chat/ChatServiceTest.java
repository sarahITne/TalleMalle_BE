package org.example.tallemalle_backend.chat;

import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.chat.model.ChatRead;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.example.tallemalle_backend.push.WebPushService;
import org.example.tallemalle_backend.recruit.RecruitRepository;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatReadRepository chatReadRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecruitRepository recruitRepository;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private WebPushService webPushService;
    @InjectMocks
    private ChatService chatService;
    @Test
    void send() {
        // given: 참여자 검증 통과 + user/recruit 조회 가능 + 저장 성공
        Long userId = 1L;
        Long recruitId = 10L;
        AuthUserDetails authUser = AuthUserDetails.builder().idx(userId).build();
        User user = buildUser(userId, "sender", "nick");
        Recruit recruit = buildRecruit(recruitId, user);

        ChatDto.SendReq dto = new ChatDto.SendReq();
        dto.setContents("hello");
        dto.setType(null);

        when(participationRepository.existsByRecruit_IdxAndUser_IdxAndStatus(recruitId, userId, ParticipationStatus.ACTIVE))
                .thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recruitRepository.findById(recruitId)).thenReturn(Optional.of(recruit));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatDto.SendRes res = chatService.send(authUser, recruitId, dto);

        // then: 저장 엔티티와 응답 DTO 매핑이 기대값인지 확인
        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());
        Chat saved = chatCaptor.getValue();

        assertEquals("message", saved.getType());
        assertEquals("hello", saved.getContents());
        assertEquals(user, saved.getUser());
        assertEquals(recruit, saved.getRecruit());

        assertEquals("message", res.getType());
        assertEquals("hello", res.getContents());
        assertEquals(recruitId, res.getRecruitIdx());
        assertEquals(userId, res.getSenderId());
        assertEquals("nick", res.getSenderName());

        // then: 푸시 알림 호출
        verify(webPushService).notifyRoom(recruitId, user, "hello");
    }

    @Test
    void list() {
        // given: 참여자 검증 통과 + 채팅 2개 + 읽음 마커 없음
        Long userId = 1L;
        Long recruitId = 10L;
        AuthUserDetails authUser = AuthUserDetails.builder().idx(userId).build();
        User user = buildUser(userId, "sender", "nick");
        Recruit recruit = buildRecruit(recruitId, user);

        Chat chat1 = buildChat(1L, "first", "message", user, recruit);
        Chat chat2 = buildChat(2L, "second", "message", user, recruit);

        when(participationRepository.existsByRecruit_IdxAndUser_IdxAndStatus(recruitId, userId, ParticipationStatus.ACTIVE))
                .thenReturn(true);
        when(chatRepository.findPageByRecruitIdxWithUserProfile(eq(recruitId), isNull(), any()))
                .thenReturn(List.of(chat2, chat1));
        when(chatReadRepository.findByUser_IdxAndRecruit_Idx(userId, recruitId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recruitRepository.findById(recruitId)).thenReturn(Optional.of(recruit));
        when(chatReadRepository.save(any(ChatRead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<ChatDto.ListRes> result = chatService.list(authUser, recruitId, null, 30);

        // then: 목록 매핑 + 마지막 읽음 인덱스 저장
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getIdx());
        assertEquals("first", result.get(0).getContents());

        ArgumentCaptor<ChatRead> readCaptor = ArgumentCaptor.forClass(ChatRead.class);
        verify(chatReadRepository).save(readCaptor.capture());
        ChatRead savedRead = readCaptor.getValue();
        assertEquals(2L, savedRead.getLastReadChatIdx());
        assertEquals(user, savedRead.getUser());
        assertEquals(recruit, savedRead.getRecruit());
    }

    @Test
    void unreadRecruitIds() {
        // given: 참여중 방 2개, 한 방만 읽지 않은 메시지 존재
        Long userId = 1L;
        AuthUserDetails authUser = AuthUserDetails.builder().idx(userId).build();
        User user = buildUser(userId, "sender", "nick");

        Recruit recruit1 = buildRecruit(10L, user);
        Recruit recruit2 = buildRecruit(20L, user);

        Participation p1 = buildParticipation(1L, user, recruit1);
        Participation p2 = buildParticipation(2L, user, recruit2);

        when(participationRepository.findAllByUser_IdxAndStatus(userId, ParticipationStatus.ACTIVE))
                .thenReturn(List.of(p1, p2));

        ChatRead read = ChatRead.builder()
                .user(user)
                .recruit(recruit1)
                .lastReadChatIdx(5L)
                .build();

        when(chatReadRepository.findByUser_IdxAndRecruit_Idx(userId, 10L)).thenReturn(Optional.of(read));
        when(chatReadRepository.findByUser_IdxAndRecruit_Idx(userId, 20L)).thenReturn(Optional.empty());
        when(chatRepository.existsByRecruit_IdxAndIdxGreaterThanAndUser_IdxNot(10L, 5L, userId)).thenReturn(true);
        when(chatRepository.existsByRecruit_IdxAndIdxGreaterThanAndUser_IdxNot(20L, 0L, userId)).thenReturn(false);

        // when
        List<Long> result = chatService.unreadRecruitIds(authUser);
        // then: 읽지 않은 방만 반환
        assertEquals(List.of(10L), result);

        // given: 참여중 방이 없는 경우
        reset(participationRepository, chatReadRepository, chatRepository);
        when(participationRepository.findAllByUser_IdxAndStatus(userId, ParticipationStatus.ACTIVE))
                .thenReturn(List.of());

        // when
        List<Long> empty = chatService.unreadRecruitIds(authUser);
        // then: 빈 리스트 반환 + 추가 조회 없음
        assertTrue(empty.isEmpty());
        verifyNoInteractions(chatReadRepository, chatRepository);
    }

    @Test
    void rooms() {
        // given: 참여중 방 1개
        Long userId = 1L;
        AuthUserDetails authUser = AuthUserDetails.builder().idx(userId).build();
        User user = buildUser(userId, "owner", "nick");
        Recruit recruit = buildRecruit(10L, user);
        Participation participation = buildParticipation(1L, user, recruit);

        when(participationRepository.findAllByUser_IdxAndStatus(userId, ParticipationStatus.ACTIVE))
                .thenReturn(List.of(participation));

        // when
        List<ChatDto.RoomRes> result = chatService.rooms(authUser);

        // then: 방 DTO 매핑 확인
        assertEquals(1, result.size());
        ChatDto.RoomRes room = result.get(0);
        assertEquals(10L, room.getRecruitIdx());
        assertEquals(recruit.getStatus().name(), room.getStatus());
        assertEquals(recruit.getStartPointName(), room.getStartPointName());
        assertEquals(recruit.getDestPointName(), room.getDestPointName());
    }

    private User buildUser(Long id, String name, String nickname) {
        User user = User.builder()
                .idx(id)
                .name(name)
                .email(name + "@test.com")
                .password("pw")
                .role("ROLE_USER")
                .status(UserStatus.IDLE)
                .enable(true)
                .build();

        Profile profile = Profile.builder()
                .idx(id)
                .user(user)
                .nickname(nickname)
                .phoneNumber("01000000000")
                .birth(LocalDate.of(2000, 1, 1))
                .gender("M")
                .build();
        user.setProfile(profile);
        return user;
    }

    private Recruit buildRecruit(Long id, User owner) {
        return Recruit.builder()
                .idx(id)
                .owner(owner)
                .description("desc")
                .startPointName("start")
                .startLat(37.0)
                .startLng(127.0)
                .destPointName("dest")
                .destLat(38.0)
                .destLng(128.0)
                .departureTime(LocalDateTime.of(2026, 1, 1, 10, 0))
                .maxCapacity(4)
                .currentCapacity(1)
                .status(RecruitStatus.RECRUITING)
                .createdAt(LocalDateTime.of(2026, 1, 1, 9, 0))
                .build();
    }

    private Chat buildChat(Long id, String contents, String type, User user, Recruit recruit) {
        return Chat.builder()
                .idx(id)
                .contents(contents)
                .type(type)
                .user(user)
                .recruit(recruit)
                .build();
    }

    private Participation buildParticipation(Long id, User user, Recruit recruit) {
        return Participation.builder()
                .idx(id)
                .user(user)
                .recruit(recruit)
                .status(ParticipationStatus.ACTIVE)
                .build();
    }
}
