package org.example.tallemalle_backend.recruit;

import org.example.tallemalle_backend.recruit.model.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    @Query("SELECT r FROM Recruit r WHERE r.startLat BETWEEN :swLat AND :neLat AND r.startLng BETWEEN :swLng AND :neLng")
    List<Recruit> findRecruitsInBounds(
            @Param("swLat") Double swLat, @Param("swLng") Double swLng,
            @Param("neLat") Double neLat, @Param("neLng") Double neLng
    );
}
