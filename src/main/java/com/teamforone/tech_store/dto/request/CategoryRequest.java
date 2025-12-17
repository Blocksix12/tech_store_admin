package com.teamforone.tech_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;

    private String description;

    private String slug;

    private String status; // "ACTIVE" hoặc "INACTIVE"

    private Integer displayOrder;

    private String imageUrl;

    private MultipartFile defaultImage;

    private String parentCategory;
}
