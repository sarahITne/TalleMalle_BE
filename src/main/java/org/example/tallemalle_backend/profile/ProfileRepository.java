package org.example.tallemalle_backend.profile;

import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
