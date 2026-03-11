package org.example.tallemalle_backend.recruit;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;

    // TODO: 페이징 처리 필요
    public List<RecruitDto.ListRes> list() {
        List<Recruit> recruitList = recruitRepository.findAll();

        return recruitList.stream().map(RecruitDto.ListRes::from).toList();
    }
}
