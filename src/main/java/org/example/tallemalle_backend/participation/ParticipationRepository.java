package org.example.tallemalle_backend.participation;

import org.example.tallemalle_backend.participation.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByRecruit_IdAndUser_Idx(Long recruitId, Long userIdx);
    java.util.List<Participation> findAllByRecruit_Id(Long recruitId);
    java.util.List<Participation> findAllByUser_Idx(Long userIdx);
}
