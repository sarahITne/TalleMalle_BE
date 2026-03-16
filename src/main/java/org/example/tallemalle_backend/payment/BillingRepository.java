package org.example.tallemalle_backend.payment;

import org.example.tallemalle_backend.payment.data.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Billing, Long> {

}
