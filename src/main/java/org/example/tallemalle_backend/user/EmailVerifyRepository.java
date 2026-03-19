package org.example.tallemalle_backend.user;

import org.example.tallemalle_backend.user.model.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {
    Optional<EmailVerify> findByUuid(String uuid);
    Optional<EmailVerify> findByEmail(String email);
}
