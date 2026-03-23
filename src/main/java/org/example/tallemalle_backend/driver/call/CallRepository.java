package org.example.tallemalle_backend.driver.call;

import jakarta.persistence.LockModeType;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CallRepository extends JpaRepository<Call, Long> {
    // Refactor : recruitIdx·departureTime — recruit를 EntityGraph로 함께 로드 (N+1 방지)
    @EntityGraph(attributePaths = "recruit")
    @Query("SELECT c FROM Call c WHERE c.status IN :statuses")
    Page<Call> findByStatusIn(@Param("statuses") List<CallStatus> statuses, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Call c where c.id = :id")
    Optional<Call> findByIdWithLock(@Param("id") Long id);

    boolean existsByDriverIdxAndStatus(Long driverIdx, CallStatus status);

    Optional<Call> findByDriverIdxAndStatus(Long driverIdx, CallStatus status);

    @Query("SELECT c FROM Call c WHERE c.driverIdx = :driverIdx AND c.status IN :statuses")
    Optional<Call> findByDriverIdxAndStatusIn(@Param("driverIdx") Long driverIdx, @Param("statuses") List<CallStatus> statuses);

    Page<Call> findAllByDriverIdxAndStatus(Long driverIdx, CallStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.estimatedFare), 0) FROM Call c WHERE c.driverIdx = :driverIdx AND c.status = :status")
    Long sumEstimatedFareByDriverIdxAndStatus(@Param("driverIdx") Long driverIdx, @Param("status") CallStatus status);

    Optional<Call> findTopByRecruit_IdxAndStatusInOrderByIdDesc(Long recruitIdx, List<CallStatus> statuses);
}
