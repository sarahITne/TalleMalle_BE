package com.tallemalle.api.recruit;

import com.tallemalle.api.recruit.model.RecruitDto;

import java.util.List;

public class RecruitService {
    private final RecruitRepository recruitRespository;

    public RecruitService(RecruitRepository recruitRespository) {
        this.recruitRespository = recruitRespository;
    }

    public List<RecruitDto.ListRes> findAll(double minLat, double minLng, double maxLat, double maxLng) {
        return recruitRespository.findAll(minLat, minLng, maxLat, maxLng);
    }

}
