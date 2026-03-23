package org.example.tallemalle_backend.chat;

import org.example.tallemalle_backend.chat.model.ChatRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {
    Optional<ChatRead> findByUser_IdxAndRecruit_Idx(Long userIdx, Long recruitId);

    @Modifying
    @Query("""
            UPDATE ChatRead cr
            SET cr.lastReadChatIdx = :lastReadChatIdx
            WHERE cr.user.idx = :userIdx
              AND cr.recruit.idx = :recruitIdx
              AND cr.lastReadChatIdx < :lastReadChatIdx
            """)
    int updateLastReadIfGreater(
            @Param("userIdx") Long userIdx,
            @Param("recruitIdx") Long recruitIdx,
            @Param("lastReadChatIdx") Long lastReadChatIdx
    );
}
