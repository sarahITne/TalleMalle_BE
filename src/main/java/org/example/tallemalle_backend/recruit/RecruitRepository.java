package org.example.tallemalle_backend.recruit;

import org.example.tallemalle_backend.recruit.model.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    @Query("SELECT r FROM Recruit r WHERE r.startLat BETWEEN :swLat AND :neLat AND r.startLng BETWEEN :swLng AND :neLng")
    List<Recruit> findRecruitsInBounds(@Param("swLat") Double swLat, @Param("swLng") Double swLng, @Param("neLat") Double neLat, @Param("neLng") Double neLng);

    // 시간 다 되고 인원이 꽉 찬 방 찾기 (FULL 상태이면서 출발 시간이 현재 이전)
    @Query("SELECT r FROM Recruit r WHERE r.status = 'FULL' AND r.departureTime <= :now")
    List<Recruit> findReadyToCall(@Param("now") LocalDateTime now);

    // 출발 시간 20분 초과한 미출발 방 찾기 (DRIVING, END가 아닌 방)
    @Query("SELECT r FROM Recruit r WHERE r.status IN ('RECRUITING', 'FULL', 'CALLING') AND r.departureTime <= :limitTime")
    List<Recruit> findExpiredRecruits(@Param("limitTime") LocalDateTime limitTime);
}
