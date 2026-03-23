package org.example.tallemalle_backend.notice;

import org.example.tallemalle_backend.notice.model.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("select n from Notice n " +
            "join fetch n.user u " +
            "join fetch u.profile " +
            "order by n.isPinned desc, n.idx desc")
    Slice<Notice> findAllSliceBy(Pageable pageable);
}
