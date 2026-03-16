package org.example.tallemalle_backend.notification;

import org.example.tallemalle_backend.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 전체 알림 조회
    List<Notification> findAllByUserIdx(Long userIdx);

    Page<Notification> findAllByUserIdx(Long userIdx, Pageable pageable);

    Optional<Notification> findByUserIdxAndIdx(Long userIdx, Long idx);
    // 최신 5개 알림 조회
    List<Notification> findTop5ByUserIdxOrderByCreatedAtDesc(Long userIdx);
}
