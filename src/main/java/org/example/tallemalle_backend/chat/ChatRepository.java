package org.example.tallemalle_backend.chat;

import org.example.tallemalle_backend.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByRecruit_IdxOrderByIdxAsc(Long recruitIdx);
    boolean existsByRecruit_IdxAndIdxGreaterThanAndUser_IdxNot(Long recruitIdx, Long idx, Long userIdx);
}
