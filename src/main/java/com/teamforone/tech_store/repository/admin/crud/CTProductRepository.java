package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.CTProductId;
import com.teamforone.tech_store.model.CTProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CTProductRepository extends JpaRepository<CTProducts, CTProductId> {
}
