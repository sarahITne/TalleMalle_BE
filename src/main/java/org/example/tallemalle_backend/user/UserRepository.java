package org.example.tallemalle_backend.user;

import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProfileNickname(String nickname);
}