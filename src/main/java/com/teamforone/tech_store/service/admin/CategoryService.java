package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.CategoryRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Categories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Categories> getAllCategories();
    Categories addCategory(CategoryRequest request);
    Categories updateCategory(String id, CategoryRequest request) throws IOException;
    void deleteCategory(String id);
    Categories findCategoryById(String id);
    Optional<Categories> findCategoryByName(String categoryName);
}
