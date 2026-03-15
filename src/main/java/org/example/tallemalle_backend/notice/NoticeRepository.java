package org.example.tallemalle_backend.notice;

import org.example.tallemalle_backend.notice.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
