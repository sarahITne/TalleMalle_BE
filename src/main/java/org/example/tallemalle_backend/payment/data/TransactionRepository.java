package org.example.tallemalle_backend.payment.data;

import org.example.tallemalle_backend.payment.data.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
