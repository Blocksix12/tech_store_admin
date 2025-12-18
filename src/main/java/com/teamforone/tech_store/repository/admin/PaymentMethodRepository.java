package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
}
