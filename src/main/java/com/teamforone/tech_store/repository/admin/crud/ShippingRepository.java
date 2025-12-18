package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, String> {
}