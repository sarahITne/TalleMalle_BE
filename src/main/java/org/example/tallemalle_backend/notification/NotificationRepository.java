package org.example.tallemalle_backend.notification;

import org.example.tallemalle_backend.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserIdx(Long userIdx, Pageable pageable);

    Optional<Notification> findByUserIdxAndIdx(Long userIdx, Long idx);
    // 최신 5개 알림 조회
    List<Notification> findTop5ByUserIdxOrderByCreatedAtDesc(Long userIdx);

    // Refactor : 알림을 전부 로드한 뒤 행마다 save 하던 방식 대신 한번으로 읽음 처리하도록 변경
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.idx = :userIdx AND n.isRead = false")
    int markAllReadByUserIdx(@Param("userIdx") Long userIdx);
}
