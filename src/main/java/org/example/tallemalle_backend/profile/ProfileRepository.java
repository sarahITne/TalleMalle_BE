package org.example.tallemalle_backend.profile;

import org.example.tallemalle_backend.profile.data.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // 닉네임 중복확인 기능 관련
    Optional<Profile> findByNickname(String nickname);
}
