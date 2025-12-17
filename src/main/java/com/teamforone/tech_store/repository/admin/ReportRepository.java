package com.teamforone.tech_store.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<com.teamforone.tech_store.model.Orders, String> {

    // ===== DASHBOARD STATISTICS =====

    // ✅ FIX: Sử dụng CAST để đảm bảo kiểu dữ liệu đúng
    @Query(value = """
        SELECT 
            CAST(COUNT(*) AS SIGNED) as order_count,
            CAST(COALESCE(SUM(total_amount), 0) AS DECIMAL(19,2)) as total_revenue
        FROM orders
        WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
    """, nativeQuery = true)
    Object[] getTotalOrdersAndRevenue();

    @Query(value = """
        SELECT 
            CAST(COUNT(*) AS SIGNED) as order_count,
            CAST(COALESCE(SUM(total_amount), 0) AS DECIMAL(19,2)) as total_revenue
        FROM orders
        WHERE DATE(created_at) = CURDATE()
    """, nativeQuery = true)
    Object[] getTodayOrdersAndRevenue();

    @Query(value = """
        SELECT 
            CASE 
                WHEN total_stock = 0 THEN 'OUT_OF_STOCK'
                WHEN total_stock <= 10 THEN 'LOW_STOCK'
                ELSE 'IN_STOCK'
            END as stock_status,
            CAST(COUNT(*) AS SIGNED) as count
        FROM (
            SELECT 
                p.product_id,
                CAST(COALESCE(SUM(ct.quantity), 0) AS SIGNED) as total_stock
            FROM products p
            LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
            GROUP BY p.product_id
        ) as stock_data
        GROUP BY stock_status
    """, nativeQuery = true)
    List<Object[]> getStockStatus();

    // ===== BEST SELLING PRODUCTS =====

    @Query(value = """
        SELECT 
            p.product_id,
            p.name,
            p.default_image,
            COALESCE(c.category_name, 'Chưa phân loại'),
            COALESCE(b.brand_name, 'Chưa có thương hiệu'),
            CAST(COALESCE(SUM(oi.quantity), 0) AS SIGNED) as total_sold,
            CAST(COALESCE(SUM(oi.subtotal), 0) AS DECIMAL(19,2)) as total_revenue
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.category_id
        LEFT JOIN brands b ON p.brand_id = b.brand_id
        LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
        LEFT JOIN order_items oi ON ct.colorID = oi.colorID 
            AND ct.sizeID = oi.sizeID 
            AND ct.storageID = oi.storageID
        LEFT JOIN orders o ON oi.order_id = o.order_id
        WHERE (:startDate IS NULL OR DATE(o.created_at) >= :startDate)
          AND (:endDate IS NULL OR DATE(o.created_at) <= :endDate)
        GROUP BY p.product_id, p.name, p.default_image, c.category_name, b.brand_name
        ORDER BY total_sold DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> getBestSellingProducts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit);

    // ===== INVENTORY REPORT =====

    @Query(value = """
        SELECT 
            p.product_id,
            p.name,
            p.default_image,
            COALESCE(c.category_name, 'Chưa phân loại'),
            COALESCE(b.brand_name, 'Chưa có thương hiệu'),
            CAST(COALESCE(SUM(ct.quantity), 0) AS SIGNED) as total_stock,
            CAST(COUNT(DISTINCT CONCAT(COALESCE(ct.colorID, ''), 
                                       COALESCE(ct.sizeID, ''), 
                                       COALESCE(ct.storageID, ''))) AS SIGNED) as variant_count
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.category_id
        LEFT JOIN brands b ON p.brand_id = b.brand_id
        LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
        GROUP BY p.product_id, p.name, p.default_image, c.category_name, b.brand_name
        ORDER BY total_stock ASC, p.name ASC
    """, nativeQuery = true)
    List<Object[]> getInventoryReport();

    // ===== REVENUE REPORTS =====

    @Query(value = """
        SELECT 
            DATE_FORMAT(o.created_at, '%Y-%m-%d') as period,
            CAST(COALESCE(SUM(o.total_amount), 0) AS DECIMAL(19,2)) as total_revenue,
            CAST(COUNT(o.order_id) AS SIGNED) as total_orders
        FROM orders o
        WHERE (:startDate IS NULL OR DATE(o.created_at) >= :startDate)
          AND (:endDate IS NULL OR DATE(o.created_at) <= :endDate)
        GROUP BY period
        ORDER BY period DESC
    """, nativeQuery = true)
    List<Object[]> getRevenueByDay(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT 
            DATE_FORMAT(o.created_at, '%Y-%m') as period,
            CAST(COALESCE(SUM(o.total_amount), 0) AS DECIMAL(19,2)) as total_revenue,
            CAST(COUNT(o.order_id) AS SIGNED) as total_orders
        FROM orders o
        WHERE (:startDate IS NULL OR DATE(o.created_at) >= :startDate)
          AND (:endDate IS NULL OR DATE(o.created_at) <= :endDate)
        GROUP BY period
        ORDER BY period DESC
    """, nativeQuery = true)
    List<Object[]> getRevenueByMonth(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT 
            CONCAT(YEAR(o.created_at), '-W', LPAD(WEEK(o.created_at), 2, '0')) as period,
            CAST(COALESCE(SUM(o.total_amount), 0) AS DECIMAL(19,2)) as total_revenue,
            CAST(COUNT(o.order_id) AS SIGNED) as total_orders
        FROM orders o
        WHERE (:startDate IS NULL OR DATE(o.created_at) >= :startDate)
          AND (:endDate IS NULL OR DATE(o.created_at) <= :endDate)
        GROUP BY period
        ORDER BY period DESC
    """, nativeQuery = true)
    List<Object[]> getRevenueByWeek(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT 
            CAST(YEAR(o.created_at) AS CHAR) as period,
            CAST(COALESCE(SUM(o.total_amount), 0) AS DECIMAL(19,2)) as total_revenue,
            CAST(COUNT(o.order_id) AS SIGNED) as total_orders
        FROM orders o
        WHERE (:startDate IS NULL OR DATE(o.created_at) >= :startDate)
          AND (:endDate IS NULL OR DATE(o.created_at) <= :endDate)
        GROUP BY period
        ORDER BY period DESC
    """, nativeQuery = true)
    List<Object[]> getRevenueByYear(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // ===== CATEGORY PERFORMANCE =====

    @Query(value = """
        SELECT 
            c.category_id,
            c.category_name,
            CAST(COUNT(DISTINCT p.product_id) AS SIGNED) as product_count,
            CAST(COALESCE(SUM(oi.quantity), 0) AS SIGNED) as total_sold,
            CAST(COALESCE(SUM(oi.subtotal), 0) AS DECIMAL(19,2)) as total_revenue
        FROM categories c
        LEFT JOIN products p ON c.category_id = p.category_id
        LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
        LEFT JOIN order_items oi ON ct.colorID = oi.colorID 
            AND ct.sizeID = oi.sizeID 
            AND ct.storageID = oi.storageID
        GROUP BY c.category_id, c.category_name
        ORDER BY total_revenue DESC
    """, nativeQuery = true)
    List<Object[]> getCategoryPerformance();

    // ===== BRAND PERFORMANCE =====

    @Query(value = """
        SELECT 
            b.brand_id,
            b.brand_name,
            b.logo_url,
            CAST(COUNT(DISTINCT p.product_id) AS SIGNED) as product_count,
            CAST(COALESCE(SUM(oi.quantity), 0) AS SIGNED) as total_sold,
            CAST(COALESCE(SUM(oi.subtotal), 0) AS DECIMAL(19,2)) as total_revenue
        FROM brands b
        LEFT JOIN products p ON b.brand_id = p.brand_id
        LEFT JOIN ctproducts ct ON p.product_id = ct.product_id
        LEFT JOIN order_items oi ON ct.colorID = oi.colorID 
            AND ct.sizeID = oi.sizeID 
            AND ct.storageID = oi.storageID
        GROUP BY b.brand_id, b.brand_name, b.logo_url
        ORDER BY total_revenue DESC
    """, nativeQuery = true)
    List<Object[]> getBrandPerformance();
}