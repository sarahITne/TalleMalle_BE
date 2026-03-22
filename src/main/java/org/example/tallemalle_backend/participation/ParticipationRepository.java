package org.example.tallemalle_backend.participation;

import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByUserAndRecruit(User user, Recruit recruit);
    Optional<Participation> findByUserIdxAndRecruitIdx(Long UserIdx, Long recruitIdx);
    List<Participation> findAllByRecruit_Idx(Long recruitId);
    List<Participation> findAllByUser_Idx(Long userIdx);
    boolean existsByRecruit_IdxAndUser_IdxAndStatus(Long recruitId, Long userIdx, ParticipationStatus status);
    List<Participation> findAllByRecruit_IdxAndStatus(Long recruitId, ParticipationStatus status);
    @Query("""
            SELECT p
            FROM Participation p
            JOIN FETCH p.user u
            LEFT JOIN FETCH u.profile prof
            WHERE p.recruit.idx = :recruitId
              AND p.status = :status
            """)
    List<Participation> findAllByRecruitIdxAndStatusWithUserProfile(
            @Param("recruitId") Long recruitId,
            @Param("status") ParticipationStatus status
    );
    List<Participation> findAllByUser_IdxAndStatus(Long userIdx, ParticipationStatus status);
}
