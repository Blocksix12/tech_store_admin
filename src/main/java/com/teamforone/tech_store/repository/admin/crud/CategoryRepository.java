package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, String> {
    // Tìm các danh mục con của 1 danh mục
    List<Categories> findByParentCategory(Categories parentCategory);

    // Tìm tất cả danh mục gốc (không có cha)
    List<Categories> findByParentCategoryIsNull();

    // Tìm theo slug
    Categories findBySlug(String slug);
}
