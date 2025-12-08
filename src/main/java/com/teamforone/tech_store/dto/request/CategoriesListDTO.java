package com.teamforone.tech_store.dto.request;
import com.teamforone.tech_store.model.Categories;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriesListDTO {
    private String categoryId;
    private String categoryName;
    private String description;
    private String slug;
    private Categories. Status status;
    private Integer displayOrder;
    private String imageUrl;

    // ✅ Stats
    private Long productCount;
    private Long ownProductCount;
    @Builder.Default
    private List<CategoriesListDTO> subCategories = new ArrayList<>();
    private Categories parentCategory;

    public boolean hasChildren() {
        return subCategories != null && !subCategories. isEmpty();
    }

    public boolean isRoot() {
        return parentCategory == null;
    }

    // Timestamps
    private String createdAt;
    private String updatedAt;
}
