package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.CategoriesListDTO;
import com.teamforone.tech_store.dto.request.CategoryRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Categories;
import com.teamforone.tech_store.service.admin.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public String getAllCategories(
            @RequestParam(required = false) String status,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Get flat list with stats
            List<CategoriesListDTO> allCategories = categoryService.getAllCategoriesWithStats();

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                try {
                    Categories. Status categoryStatus = Categories.Status.valueOf(status.toUpperCase());
                    allCategories = allCategories. stream()
                            .filter(c -> c.getStatus() == categoryStatus)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // Keep all categories
                }
            }

            // Build tree structure
            List<CategoriesListDTO> rootCategories = buildCategoryTree(allCategories);

            // ✅ NEW: Calculate cumulative product counts
            calculateCumulativeProductCounts(rootCategories);

            // Calculate totals
            long totalCategories = allCategories.size();
            long activeCategories = allCategories.stream()
                    . filter(c -> c.getStatus() == Categories.Status.ACTIVE)
                    .count();
            long inactiveCategories = totalCategories - activeCategories;

            long totalProducts = rootCategories.stream()
                    . mapToLong(c -> c.getProductCount() != null ? c.getProductCount() : 0L)
                    .sum();

            // Add to model
            model.addAttribute("categories", rootCategories);
            model.addAttribute("totalCategories", totalCategories);
            model. addAttribute("activeCategories", activeCategories);
            model.addAttribute("inactiveCategories", inactiveCategories);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("categoryStatuses", Categories.Status.values());

            // Data for topbar
            model.addAttribute("pageTitle", "Quản lý Danh mục");
            model.addAttribute("searchPlaceholder", "Tìm kiếm danh mục.. .");
            model.addAttribute("searchId", "searchCategories");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Sản phẩm", "url", "/admin/products"));
            breadcrumbs.add(Map.of("name", "Danh mục", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "Categories";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin";
        }
    }

    /**
     * ✅ Build tree structure from flat list
     */
    private List<CategoriesListDTO> buildCategoryTree(List<CategoriesListDTO> allCategories) {
        Map<String, CategoriesListDTO> categoryMap = new HashMap<>();
        for (CategoriesListDTO category : allCategories) {
            categoryMap.put(category.getCategoryId(), category);
            if (category.getSubCategories() == null) {
                category.setSubCategories(new ArrayList<>());
            }
        }

        List<CategoriesListDTO> rootCategories = new ArrayList<>();

        for (CategoriesListDTO category : allCategories) {
            if (category.getParentCategory() == null) {
                rootCategories.add(category);
            } else {
                String parentId = category.getParentCategory(). getCategoryId();
                CategoriesListDTO parent = categoryMap.get(parentId);
                if (parent != null) {
                    parent.getSubCategories().add(category);
                } else {
                    rootCategories. add(category);
                }
            }
        }

        sortCategoriesByDisplayOrder(rootCategories);

        return rootCategories;
    }

    private void calculateCumulativeProductCounts(List<CategoriesListDTO> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        for (CategoriesListDTO category : categories) {
            // Recursively calculate for children first
            if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
                calculateCumulativeProductCounts(category.getSubCategories());
            }

            // ✅ Start with own count (never changes)
            long ownCount = category.getOwnProductCount() != null ? category.getOwnProductCount() : 0L;

            // ✅ Add children's cumulative counts
            long childrenTotal = 0L;
            if (category.getSubCategories() != null) {
                for (CategoriesListDTO child : category.getSubCategories()) {
                    childrenTotal += (child.getProductCount() != null ? child.getProductCount() : 0L);
                }
            }

            // ✅ Set cumulative count
            category.setProductCount(ownCount + childrenTotal);
        }
    }

    private long calculateTotalProducts(CategoriesListDTO category) {
        // Start with own product count
        long total = category.getProductCount() != null ? category.getProductCount() : 0L;

        // Add product counts from all children recursively
        if (category.getSubCategories() != null && !category.getSubCategories(). isEmpty()) {
            for (CategoriesListDTO child : category. getSubCategories()) {
                total += calculateTotalProducts(child);
            }
        }

        return total;
    }

    private void sortCategoriesByDisplayOrder(List<CategoriesListDTO> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        categories.sort((a, b) -> {
            int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : 0;
            int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : 0;
            if (orderA != orderB) {
                return Integer.compare(orderA, orderB);
            }
            return a.getCategoryName().compareTo(b.getCategoryName());
        });

        for (CategoriesListDTO category : categories) {
            if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
                sortCategoriesByDisplayOrder(category.getSubCategories());
            }
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
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
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
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public Categories findCategoryById(@PathVariable String id) {
        return categoryService.findCategoryById(id);
    }
}
