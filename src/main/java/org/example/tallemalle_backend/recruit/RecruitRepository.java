package org.example.tallemalle_backend.recruit;

import org.example.tallemalle_backend.recruit.model.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
}
