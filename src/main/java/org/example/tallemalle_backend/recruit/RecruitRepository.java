package org.example.tallemalle_backend.recruit;

import jakarta.persistence.LockModeType;
import org.example.tallemalle_backend.recruit.model.Recruit;
import org.example.tallemalle_backend.recruit.model.RecruitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    // 지도 검색 시 방장(owner) Fetch Join
    @Query("SELECT r FROM Recruit r JOIN FETCH r.owner WHERE r.startLat BETWEEN :swLat AND :neLat AND r.startLng BETWEEN :swLng AND :neLng")
    List<Recruit> findRecruitsInBounds(@Param("swLat") Double swLat, @Param("swLng") Double swLng, @Param("neLat") Double neLat, @Param("neLng") Double neLng);

    // 전체 목록 조회 시 방장(owner) Fetch Join
    @Query("SELECT r FROM Recruit r JOIN FETCH r.owner")
    List<Recruit> findAllWithFetchJoin();

    // join 시 비관적 락(Pessimistic Lock) 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Recruit r WHERE r.idx = :idx")
    Optional<Recruit> findByIdForUpdate(@Param("idx") Long idx);

    // 시간 다 되고 인원이 꽉 찬 방 찾기 (FULL 상태이면서 출발 시간이 현재 이전)
    @Query("SELECT r FROM Recruit r WHERE r.status = :status AND r.departureTime <= :now")
    List<Recruit> findReadyToCall(@Param("status") RecruitStatus status, @Param("now") LocalDateTime now);

    // 출발 시간 20분 초과한 미출발 방 찾기
    @Query("SELECT r FROM Recruit r WHERE r.status IN :statuses AND r.departureTime <= :limitTime")
    List<Recruit> findExpiredRecruits(@Param("statuses") List<RecruitStatus> statuses, @Param("limitTime") LocalDateTime limitTime);
}
