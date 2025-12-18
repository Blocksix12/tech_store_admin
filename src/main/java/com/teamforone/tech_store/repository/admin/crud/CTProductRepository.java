package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.CTProductId;
import com.teamforone.tech_store.model.CTProducts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CTProductRepository extends JpaRepository<CTProducts, CTProductId> {
    @Query("SELECT ct FROM CTProducts ct WHERE " +
            "ct.productId = :productId AND " +
            "ct.colorId = :colorId AND " +
            "(:storageId IS NULL OR ct.storageId = :storageId) AND " +
            "(:sizeId IS NULL OR ct.sizeId = :sizeId)")
    Optional<CTProducts> findByCompositeKey(
            @Param("productId") String productId,
            @Param("colorId") String colorId,
            @Param("storageId") String storageId,
            @Param("sizeId") String sizeId
    );

    @Transactional
    @Modifying
    void deleteByProductId(String productId);
}
