package org.example.tallemalle_backend.payment.data;

import org.example.tallemalle_backend.payment.common.OrderStatus;
import org.example.tallemalle_backend.payment.data.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o JOIN FETCH o.recruit LEFT JOIN FETCH o.transaction WHERE o.user.idx = :userIdx AND o.status = :status ORDER BY o.recruit.departureTime DESC")
    Page<Order> findAllByUserIdxAndStatusWithRecruit(@Param("userIdx") Long userIdx, @Param("status") OrderStatus status, Pageable pageable);
}
