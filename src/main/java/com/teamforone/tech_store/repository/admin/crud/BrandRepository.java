package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.enums.BrandStatus;
import com.teamforone.tech_store.model.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brands, String> {
    List<Brands> findByStatus(BrandStatus status);

    long countByStatus(BrandStatus status);

    List<Brands> findByStatusOrderByDisplayOrderAsc(BrandStatus status);

    List<Brands> findAllByOrderByDisplayOrderAsc();

    boolean existsByBrandNameAndStatus(String brandName, BrandStatus status);
}
