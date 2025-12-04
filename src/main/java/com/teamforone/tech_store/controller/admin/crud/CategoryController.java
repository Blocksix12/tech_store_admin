package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.CategoryRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Categories;
import com.teamforone.tech_store.service.admin.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String getAllCategories(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Categories> categories = categoryService.getAllCategories();

            // Tính toán thống kê
            long totalCategories = categories.size();
            long activeCategories = categories.stream()
                    .filter(c -> c.getStatus() == Categories.Status.ACTIVE)
                    .count();
            long inactiveCategories = totalCategories - activeCategories;

            model.addAttribute("categories", categories);
            model.addAttribute("totalCategories", totalCategories);
            model.addAttribute("activeCategories", activeCategories);
            model.addAttribute("inactiveCategories", inactiveCategories);
            model.addAttribute("pageTitle", "Quản lý Danh mục");

            return "Categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin";
        }
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("categoryStatuses", Categories.Status.values());
        model.addAttribute("pageTitle", "Thêm Danh mục");

        // Breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
        breadcrumbs.add(Map.of("name", "Danh mục", "url", "/admin/categories"));
        breadcrumbs.add(Map. of("name", "Thêm", "url", ""));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "AddCategory";
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid @ModelAttribute CategoryRequest categoryRequest,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("categoryStatuses", Categories.Status.values());
            return "AddCategory";
        }

        try {
            categoryService.addCategory(categoryRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm danh mục '" + categoryRequest.getCategoryName() + "' thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("categoryStatuses", Categories.Status.values());
            return "AddCategory";
        }
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable String id,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            Categories category = categoryService.findCategoryById(id);
            CategoryRequest categoryRequest = convertToCategoryRequest(category);

            model.addAttribute("category", category);
            model.addAttribute("categoryRequest", categoryRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("categoryStatuses", Categories.Status.values());
            model.addAttribute("pageTitle", "Chỉnh sửa Danh mục");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map. of("name", "Danh mục", "url", "/admin/categories"));
            breadcrumbs.add(Map.of("name", "Chỉnh sửa", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "EditCategory";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    // POST - Xử lý cập nhật
    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable String id,
                                 @Valid @ModelAttribute CategoryRequest categoryRequest,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Categories category = categoryService.findCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("categoryStatuses", Categories.Status.values());
            return "EditCategory";
        }

        try {
            Categories updatedCategory = categoryService.updateCategory(id, categoryRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật danh mục '" + updatedCategory.getCategoryName() + "' thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model. addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            Categories category = categoryService.findCategoryById(id);
            model. addAttribute("category", category);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("categoryStatuses", Categories.Status.values());
            return "EditCategory";
        }
    }

    // GET - Hiển thị trang xác nhận xóa
    @GetMapping("/categories/delete/{id}")
    public String showDeleteConfirm(@PathVariable String id,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            Categories category = categoryService.findCategoryById(id);

            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Xóa Danh mục");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Danh mục", "url", "/admin/categories"));
            breadcrumbs.add(Map.of("name", "Xóa", "url", ""));
            model. addAttribute("breadcrumbs", breadcrumbs);

            return "DeleteCategory";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    // POST - Xử lý xóa
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable String id,
                                 RedirectAttributes redirectAttributes) {
        try {
            Categories category = categoryService.findCategoryById(id);
            String categoryName = category.getCategoryName();

            categoryService.deleteCategory(id);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xóa danh mục '" + categoryName + "' thành công!");
            return "redirect:/admin/categories";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi xóa danh mục: " + e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    // Helper method
    private CategoryRequest convertToCategoryRequest(Categories category) {
        CategoryRequest request = new CategoryRequest();
        request.setCategoryName(category.getCategoryName());
        request.setDescription(category.getDescription());
        request.setSlug(category.getSlug());
        request.setStatus(category.getStatus() != null ? category.getStatus(). name() : "ACTIVE");
        request.setDisplayOrder(category.getDisplayOrder());
        request.setImageUrl(category.getImageUrl());

        if (category.getParentCategory() != null) {
            request. setParentCategory(category.getParentCategory().getCategoryId());
        }

        return request;
    }

    @GetMapping("/categories/{id}")
    public Categories findCategoryById(@PathVariable String id) {
        return categoryService.findCategoryById(id);
    }
}
