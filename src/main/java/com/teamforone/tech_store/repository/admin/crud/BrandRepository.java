package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.enums.BrandStatus;
import com.teamforone.tech_store.model.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brands, String> {
    List<Brands> findByStatus(BrandStatus status);

    long countByStatus(BrandStatus status);

    List<Brands> findByStatusOrderByDisplayOrderAsc(BrandStatus status);

    List<Brands> findAllByOrderByDisplayOrderAsc();

    boolean existsByBrandNameAndStatus(String brandName, BrandStatus status);
    Optional<Brands> findByBrandName(String brandName);

    @Query(value = """
        SELECT 
            b.brand_id,
            b.brand_name,
            b.description,
            b.logo_url,
            b.website_url,
            b.country,
            b. status,
            b.display_order,
            COUNT(DISTINCT p.product_id) as product_count,
            b.created_at,
            b.last_modified_at
        FROM brands b
        LEFT JOIN products p ON b.brand_id = p. brand_id
        GROUP BY b.brand_id, b.brand_name, b. description, b.logo_url, 
                 b.website_url, b.country, b.status, b.display_order,
                 b.created_at, b.last_modified_at
        ORDER BY b.display_order ASC, b.brand_name ASC
        """, nativeQuery = true)
    List<Object[]> findAllBrandsWithStats();

    // ✅ Get brand with product count and revenue
    @Query(value = """
    SELECT 
        b.brand_id,
        b.brand_name,
        b.description,
        b.logo_url,
        b.website_url,
        b.country,
        b.status,
        b.display_order,
        COUNT(DISTINCT p.product_id) as product_count,
        COALESCE(SUM(oi.subtotal), 0) as total_revenue,
        b.created_at,
        b.last_modified_at
    FROM brands b
    LEFT JOIN products p ON b.brand_id = p.brand_id
    LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
    LEFT JOIN order_items oi ON ct.colorID = oi.colorID 
        AND ct.sizeID = oi.sizeID 
        AND ct.storageID = oi.storageID
    LEFT JOIN orders o ON oi.order_id = o.order_id
    WHERE oi.order_status IN ('PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED','RETURNED') 
       OR oi.order_status IS NULL
    GROUP BY b.brand_id, b.brand_name, b.description, b.logo_url,
             b.website_url, b.country, b.status, b.display_order,
             b.created_at, b.last_modified_at
    ORDER BY total_revenue DESC
    """, nativeQuery = true)
    List<Object[]> findAllBrandsWithRevenueStats();
}
