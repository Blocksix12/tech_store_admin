package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "SELECT " +
            "p.product_id, " +
            "p.name, " +
            "p.slug, " +
            "c.category_name, " +
            "MIN(ct.sale_price) as min_price, " +
            "MAX(ct.sale_price) as max_price, " +
            "COALESCE(SUM(ct.quantity), 0) as total_quantity, " +
            "p.status, " +
            "p.default_image, " +
            "b.brand_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "LEFT JOIN ctproducts ct ON p.product_id = ct.product_id " +
            "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
            "GROUP BY p.product_id, p.name, p.slug, c.category_name, p.status, p.default_image, b.brand_name " +
            "ORDER BY p.created_at DESC",
            nativeQuery = true)
    List<Object[]> findAllProducts();

}
