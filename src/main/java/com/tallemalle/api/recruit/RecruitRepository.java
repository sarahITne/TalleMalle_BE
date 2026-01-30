package com.tallemalle.api.recruit;

import com.tallemalle.api.recruit.model.RecruitDto;

import java.util.List;

public interface RecruitRepository {
    public List<RecruitDto.ListRes> findAll(double minLat, double minLng, double maxLat, double maxLng);
}
