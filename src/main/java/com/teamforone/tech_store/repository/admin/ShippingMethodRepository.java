package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, String> {
}
