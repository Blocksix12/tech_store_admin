package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate. annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Categories {

    @Id
    @UuidGenerator
    @Column(name = "category_id", columnDefinition = "CHAR(36)")
    private String categoryId;

    @Column(name = "category_name", nullable = false, length = 255)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(name = "display_order")
    @Builder. Default
    private Integer displayOrder = 0;

    @Column(name = "image_url")
    private String imageUrl;

    // Parent-Child relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Categories parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Categories> subCategories = new ArrayList<>();

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Transient field
    @Transient
    private Integer productCount = 0;

    // Status Enum
    @Getter
    public enum Status {
        ACTIVE("Hiển thị"),
        INACTIVE("Ẩn");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public static Status toEnum(String value) {
            if (value == null) return ACTIVE;
            try {
                return Status.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ACTIVE;
            }
        }
    }

    // Helper methods
    public boolean isRoot() {
        return this.parentCategory == null;
    }

    public boolean hasChildren() {
        return this.subCategories != null && ! this.subCategories.isEmpty();
    }

    public void addSubCategory(Categories subCategory) {
        if (this.subCategories == null) {
            this.subCategories = new ArrayList<>();
        }
        this.subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Categories subCategory) {
        if (this.subCategories != null) {
            this.subCategories.remove(subCategory);
            subCategory.setParentCategory(null);
        }
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", status=" + status +
                ", parentCategory=" + (parentCategory != null ? parentCategory. getCategoryId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categories)) return false;
        Categories that = (Categories) o;
        return categoryId != null && categoryId.equals(that.getCategoryId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}