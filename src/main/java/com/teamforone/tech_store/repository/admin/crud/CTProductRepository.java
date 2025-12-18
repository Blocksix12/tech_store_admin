package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.CTProductId;
import com.teamforone.tech_store.model.CTProducts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CTProductRepository extends JpaRepository<CTProducts, CTProductId> {

    // Load đầy đủ quan hệ
    @Override
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    Optional<CTProducts> findById(CTProductId id);

    @Override
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findAll();

    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByProductId(String productId);

    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByQuantityLessThanEqual(Integer quantity);

    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByQuantity(Integer quantity);

    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    @Query("""
        SELECT c FROM CTProducts c WHERE
        (:productId IS NULL OR c.productId = :productId) AND
        (:colorId IS NULL OR c.colorId = :colorId) AND
        (:sizeId IS NULL OR c.sizeId = :sizeId) AND
        (:storageId IS NULL OR c.storageId = :storageId)
    """)
    List<CTProducts> findByFilters(
            @Param("productId") String productId,
            @Param("colorId") String colorId,
            @Param("sizeId") String sizeId,
            @Param("storageId") String storageId
    );

    long countByProductId(String productId);

    @Query("SELECT SUM(c.quantity) FROM CTProducts c WHERE c.productId = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") String productId);

    // Bổ sung từ nhánh kia
    @Query("""
        SELECT ct FROM CTProducts ct WHERE
        ct.productId = :productId AND
        ct.colorId = :colorId AND
        (:storageId IS NULL OR ct.storageId = :storageId) AND
        (:sizeId IS NULL OR ct.sizeId = :sizeId)
    """)
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
