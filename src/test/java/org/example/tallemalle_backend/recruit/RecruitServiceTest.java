package org.example.tallemalle_backend.recruit;

import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.participation.ParticipationRepository;
import org.example.tallemalle_backend.recruit.event.RecruitEvents;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecruitServiceTest {

    @InjectMocks
    private RecruitService recruitService;

    @Mock
    private RecruitRepository recruitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private User owner;
    private User joiner;
    private Recruit recruit;
    private AuthUserDetails joinerAuth;

    @BeforeEach
    void setup() {
        owner = User.builder().idx(1L).name("방장").status(UserStatus.OWNER).build();
        joiner = User.builder().idx(2L).name("참여자").status(UserStatus.IDLE).build();

        recruit = Recruit.builder()
                .idx(100L)
                .owner(owner)
                .maxCapacity(4)
                .currentCapacity(1)
                .status(RecruitStatus.RECRUITING)
                .build();

        joinerAuth = mock(AuthUserDetails.class);
        given(joinerAuth.getIdx()).willReturn(2L);
    }

    @Test
    @DisplayName("성공 : 정상적으로 모집글에 참여한다.")
    void join_success() {
        // given
        given(recruitRepository.findById(100L)).willReturn(Optional.of(recruit));
        given(userRepository.findById(2L)).willReturn(Optional.of(joiner));
        given(participationRepository.findByUserIdxAndRecruitIdx(2L, 100L)).willReturn(Optional.empty());

        // when
        boolean result = recruitService.join(joinerAuth, 100L);

        // then
        assertTrue(result);
        assertEquals(2, recruit.getCurrentCapacity());
        assertEquals(UserStatus.JOINED, joiner.getStatus());

        verify(eventPublisher).publishEvent(any(RecruitEvents.UpdatedEvent.class));
    }

    @Test
    @DisplayName("실패: 인원이 꽉 찬 방에는 참여할 수 없다")
    void join_fail_when_recruit_is_full() {
        // given
        recruit.setCurrentCapacity(4);
        recruit.setStatus(RecruitStatus.FULL);

        given(recruitRepository.findById(100L)).willReturn(Optional.of(recruit));
        given(userRepository.findById(2L)).willReturn(Optional.of(joiner));

        // when, then
        BaseException exception = assertThrows(BaseException.class, () -> {
            recruitService.join(joinerAuth, 100L);
        });

        assertEquals(BaseResponseStatus.RECRUIT_FULL, exception.getStatus());
    }
}