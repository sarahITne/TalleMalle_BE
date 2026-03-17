package org.example.tallemalle_backend.payment.data;

import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findAllByOwner(User user);
    List<Billing> findAllByOwnerOrderByCreatedAtAsc(User user);
}
