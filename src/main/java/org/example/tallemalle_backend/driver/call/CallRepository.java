package org.example.tallemalle_backend.driver.call;

import jakarta.persistence.LockModeType;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.call.model.CallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Query("SELECT c FROM Call c WHERE c.status IN :statuses")
    List<Call> findByStatusIn(@Param("statuses") List<CallStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Call c where c.id = :id")
    Optional<Call> findByIdWithLock(@Param("id") Long id);

    boolean existsByDriverIdxAndStatus(Long driverIdx, CallStatus status);

    Optional<Call> findByDriverIdx(Long driverIdx);

    List<Call> findAllByDriverIdxAndStatus(Long driverIdx, CallStatus status);
}
