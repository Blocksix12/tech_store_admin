package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.CategoryRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Categories;
import com.teamforone.tech_store.repository.admin.crud.CategoryRepository;
import com.teamforone.tech_store.service.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories addCategory(CategoryRequest request) {
        try {
            // Validate
            if (request.getCategoryName() == null || request.getCategoryName(). trim().isEmpty()) {
                throw new IllegalArgumentException("Tên danh mục không được để trống");
            }


            String imageUrl = null;
            if (request.getDefaultImage() != null && !request. getDefaultImage().isEmpty()) {
                imageUrl = fileStorageService.saveFile(request.getDefaultImage());
            }
            // Tạo category mới
            Categories newCategory = Categories.builder()
                    .categoryName(request.getCategoryName(). trim())
                    .description(request.getDescription())
                    .slug(generateSlug(request))
                    .status(Categories.Status.toEnum(request.getStatus()))
                    .displayOrder(request.getDisplayOrder() != null ? request. getDisplayOrder() : 0)
                    .imageUrl(imageUrl)
                    .build();

            // Set parent category nếu có
            if (request.getParentCategory() != null && ! request.getParentCategory().isEmpty()) {
                Categories parent = categoryRepository.findById(request.getParentCategory())
                        . orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
                newCategory.setParentCategory(parent);
            }

            // Lưu vào database
            return categoryRepository.save(newCategory);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm danh mục: " + e.getMessage());
        }
    }

    @Override
    public Categories updateCategory(String id, CategoryRequest request) throws IOException {
        Categories existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        MultipartFile newImage = request.getDefaultImage();
        if (newImage != null && !newImage.isEmpty()) {
            String oldImageUrl = existingCategory.getImageUrl(); // ✅ Lấy từ brand entity, không phải request

            // Upload ảnh mới
            String newImageUrl = fileStorageService.saveFile(newImage);
            existingCategory.setImageUrl(newImageUrl);

            // Xóa ảnh cũ (nếu tồn tại)
            if (oldImageUrl != null && ! oldImageUrl.isEmpty()) {
                try {
                    fileStorageService. deleteFile(oldImageUrl);
                } catch (Exception e) {
                    System.err.println("Không thể xóa ảnh cũ: " + e. getMessage());
                }
            }
        }


        // Update fields
        existingCategory.setCategoryName(request.getCategoryName().trim());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setSlug(generateSlug(request));
        existingCategory.setStatus(Categories.Status.toEnum(request.getStatus()));
        existingCategory.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        // Update parent
        if (request.getParentCategory() != null && !request. getParentCategory().isEmpty()) {
            Categories parent = categoryRepository. findById(request.getParentCategory())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            existingCategory.setParentCategory(parent);
        } else {
            existingCategory.setParentCategory(null);
        }

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(String id) {
        Categories existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        // Kiểm tra có danh mục con không
        List<Categories> childCategories = categoryRepository.findByParentCategory(existingCategory);
        if (childCategories != null && !childCategories.isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục có danh mục con.  Vui lòng xóa danh mục con trước!");
        }

        categoryRepository.delete(existingCategory);
    }

    @Override
    public Categories findCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
    }

    // Helper method: Tạo slug tự động
    private String generateSlug(CategoryRequest request) {
        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            return request.getSlug().trim().toLowerCase();
        }

        // Tạo slug từ categoryName
        String slug = request. getCategoryName()
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                . replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        return slug;
    }
}
