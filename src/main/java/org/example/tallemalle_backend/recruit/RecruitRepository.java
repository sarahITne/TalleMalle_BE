package org.example.tallemalle_backend.recruit;

import jakarta.persistence.LockModeType;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    // 지도 검색 시 방장(owner) Fetch Join
    @Query("SELECT r FROM Recruit r " +
            "JOIN FETCH r.owner " + // 1:1 관계인 owner는 페치 조인
            "WHERE r.startLat BETWEEN :swLat AND :neLat " +
            "AND r.startLng BETWEEN :swLng AND :neLng " +
            "AND r.status != :status")
    Slice<Recruit> findActiveRecruitsInBounds(
            @Param("swLat") Double swLat,
            @Param("swLng") Double swLng,
            @Param("neLat") Double neLat,
            @Param("neLng") Double neLng,
            @Param("status") RecruitStatus status,
            Pageable pageable);

    // 전체 목록 조회 시 방장(owner) Fetch Join
    @Query("SELECT r FROM Recruit r JOIN FETCH r.owner")
    List<Recruit> findAllWithFetchJoin();

    // join 시 비관적 락(Pessimistic Lock) 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Recruit r WHERE r.idx = :idx")
    Optional<Recruit> findByIdForUpdate(@Param("idx") Long idx);

    // 인원 FULL · 아직 콜 없음 · 출발 만료 전 → 출발 시각 이전에 드라이버에게 콜 노출(수락·이동 시간 확보)
    // notExpiredAfter = now - grace 분, findExpiredRecruits와 동일 grace로 출발 직후 소량 지연 보정
    @Query("SELECT r FROM Recruit r WHERE r.status = :status "
            + "AND NOT EXISTS (SELECT 1 FROM Call c WHERE c.recruit.idx = r.idx) "
            + "AND r.departureTime > :notExpiredAfter")
    List<Recruit> findReadyToCall(@Param("status") RecruitStatus status,
            @Param("notExpiredAfter") LocalDateTime notExpiredAfter);

    // 출발 시간 20분 초과한 미출발 방 찾기
    @Query("SELECT r FROM Recruit r WHERE r.status IN :statuses AND r.departureTime <= :limitTime")
    List<Recruit> findExpiredRecruits(@Param("statuses") List<RecruitStatus> statuses, @Param("limitTime") LocalDateTime limitTime);
}
