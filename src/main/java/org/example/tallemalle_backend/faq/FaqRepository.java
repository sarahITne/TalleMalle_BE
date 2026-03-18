package org.example.tallemalle_backend.faq;

import org.example.tallemalle_backend.faq.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
