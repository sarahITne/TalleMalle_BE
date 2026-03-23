package org.example.tallemalle_backend.driver.auth;

import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverUserRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByNickname(String nickname);
}
