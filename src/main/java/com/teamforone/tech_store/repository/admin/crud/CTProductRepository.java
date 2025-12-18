package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.CTProductId;
import com.teamforone.tech_store.model.CTProducts;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
import java.util.Optional;

@Repository
public interface CTProductRepository extends JpaRepository<CTProducts, CTProductId> {
<<<<<<< HEAD

    // ✅ Override findById để load eager relationships
    @Override
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    Optional<CTProducts> findById(CTProductId id);

    // ✅ Override findAll để load eager relationships
    @Override
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findAll();

    // ✅ Tìm tất cả variants của 1 product
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByProductId(String productId);

    // ✅ Tìm các sản phẩm có số lượng <= threshold
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByQuantityLessThanEqual(Integer quantity);

    // ✅ Tìm các sản phẩm hết hàng
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    List<CTProducts> findByQuantity(Integer quantity);

    // ✅ Tìm variants theo nhiều điều kiện
    @EntityGraph(attributePaths = {"product", "color", "size", "storage"})
    @Query("SELECT c FROM CTProducts c WHERE " +
            "(:productId IS NULL OR c.productId = :productId) AND " +
            "(:colorId IS NULL OR c.colorId = :colorId) AND " +
            "(:sizeId IS NULL OR c.sizeId = :sizeId) AND " +
            "(:storageId IS NULL OR c.storageId = :storageId)")
    List<CTProducts> findByFilters(
            @Param("productId") String productId,
            @Param("colorId") String colorId,
            @Param("sizeId") String sizeId,
            @Param("storageId") String storageId
    );

    // Đếm số lượng variants của 1 product (không cần EntityGraph)
    long countByProductId(String productId);

    // Tính tổng số lượng tồn kho của 1 product (không cần EntityGraph)
    @Query("SELECT SUM(c.quantity) FROM CTProducts c WHERE c.productId = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") String productId);
}
=======
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
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
