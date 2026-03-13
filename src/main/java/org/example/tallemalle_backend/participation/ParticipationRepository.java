package org.example.tallemalle_backend.participation;

import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByUserAndRecruit(User user, Recruit recruit);
}
