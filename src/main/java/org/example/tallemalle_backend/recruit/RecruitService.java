package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.recruit.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.user.UserRepository;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;

    // TODO: Socket 통신 연결 필요
    public void reg(AuthUserDetails user, RecruitDto.RegReq dto) {
        User realUser = userRepository.findById(user.getIdx()).orElseThrow();

        Recruit recruit = dto.toEntity(realUser);

        Participation participation = Participation.builder()
                .user(realUser)
                .recruit(recruit)
                .status("ACTIVE")
                .build();

        recruit.getParticipations().add(participation);
        realUser.setCurrentRecruit(recruit);
        recruitRepository.save(recruit);
    }

    // TODO: Slice로 페이징 처리 필요
    public List<RecruitDto.ListRes> list() {
        List<Recruit> recruitList = recruitRepository.findAll();

        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }

}
