package org.example.tallemalle_backend.chat;

import org.example.tallemalle_backend.chat.model.ChatRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {
    Optional<ChatRead> findByUser_IdxAndRecruit_Id(Long userIdx, Long recruitId);
}
