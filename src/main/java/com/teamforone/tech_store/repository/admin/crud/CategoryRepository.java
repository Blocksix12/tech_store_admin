package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, String> {
    // Tìm các danh mục con của 1 danh mục
    List<Categories> findByParentCategory(Categories parentCategory);

    // Tìm tất cả danh mục gốc (không có cha)
    List<Categories> findByParentCategoryIsNull();

    // Tìm theo slug
    Categories findBySlug(String slug);
    Optional<Categories> findByCategoryName(String categoryName);
    @Query(value = """
        SELECT 
            c.category_id,
            c.category_name,
            c.description,
            c.slug,
            c.status,
            c. display_order,
            c.image_url,
            c.parent_id,
            parent. category_name as parent_name,
            COUNT(DISTINCT p.product_id) as product_count,
            c.created_at,
            c.updated_at
        FROM categories c
        LEFT JOIN categories parent ON c. parent_id = parent.category_id
        LEFT JOIN products p ON c.category_id = p. category_id
        GROUP BY c.category_id, c.category_name, c.description, c.slug,
                 c.status, c.display_order, c.image_url, c.parent_id,
                 parent.category_name, c.created_at, c. updated_at
        ORDER BY c.display_order ASC, c.category_name ASC
        """, nativeQuery = true)
    List<Object[]> findAllCategoriesWithStats();
}
