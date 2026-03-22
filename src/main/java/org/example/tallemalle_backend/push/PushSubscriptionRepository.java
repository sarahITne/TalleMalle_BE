package org.example.tallemalle_backend.push;

import org.example.tallemalle_backend.push.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findAllByUser_IdxIn(List<Long> userIdxList);

    List<PushSubscription> findAllByUser_Idx(Long userIdx);

    boolean existsByUser_IdxAndEndpoint(Long userIdx, String endpoint);
}
