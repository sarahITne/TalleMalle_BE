package org.example.tallemalle_backend.chat;

import org.example.tallemalle_backend.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByRecruit_IdxOrderByIdxAsc(Long recruitIdx);
    @Query("""
            SELECT c
            FROM Chat c
            JOIN FETCH c.user u
            LEFT JOIN FETCH u.profile p
            WHERE c.recruit.idx = :recruitIdx
            ORDER BY c.idx ASC
            """)
    List<Chat> findAllByRecruitIdxWithUserProfileOrderByIdxAsc(@Param("recruitIdx") Long recruitIdx);
    boolean existsByRecruit_IdxAndIdxGreaterThanAndUser_IdxNot(Long recruitIdx, Long idx, Long userIdx);
}
