package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    @Id
    @UuidGenerator
    @Column(name = "product_id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

//    @ManyToOne
//    @JoinColumn(name = "brand_id", nullable = false)
//    private Brands brandId;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    private Categories categoryId;

    @Column(name = "brand_id", nullable = false, columnDefinition = "CHAR(36)")
    private String brandId;

    @Column(name = "category_id", nullable = false, columnDefinition = "CHAR(36)")
    private String categoryId;

    @Column(name = "default_image", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('draft','published','archived') DEFAULT 'published'")
    private Status productStatus = Status.PUBLISHED;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "updated_at")
    private Date updatedAt;

    @Getter
    public enum Status {
        DRAFT("Nháp"),
        PUBLISHED("Xuất bản"),
        ARCHIVED("Lưu trữ");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }
        public static Status toEnum(String type) {
            for (Status item : values()) {
                if (item.name().equalsIgnoreCase(type)) return item;
            }
            return null;
        }

        public static Status fromDisplayName(String displayName) {
            if (displayName == null || displayName.isEmpty()) return null;
            for (Status item : values()) {
                if (item.getDisplayName().equalsIgnoreCase(displayName)) return item;
            }
            return null;
        }

        // In Product.Status enum
        public static Status fromString(String value) {
            if (value == null || value.isEmpty()) return null;

            // Remove accents and normalize
            String normalized = removeVietnameseAccents(value).toUpperCase().trim();

            // Try enum name first
            try {
                return Status.valueOf(normalized);
            } catch (IllegalArgumentException e) {
                // Continue to mapping
            }

            // Map Vietnamese and English
            switch (normalized) {
                // PUBLISHED variants
                case "DANG BAN":
                case "ĐANG BÁN":
                case "DANGBAN":
                case "ACTIVE":
                case "XUAT BAN":
                case "XUẤT BẢN":
                case "XUATBAN":
                case "PUBLISHED":
                    return PUBLISHED;

                // ARCHIVED variants
                case "NGUNG BAN":
                case "NGỪNG BÁN":
                case "NGUNGBAN":
                case "INACTIVE":
                case "LUU TRU":
                case "LƯU TRỮ":
                case "LUUTRU":
                case "ARCHIVED":
                    return ARCHIVED;

                // DRAFT variants
                case "NHAP":
                case "NHÁP":
                case "DRAFT":
                    return DRAFT;

                default:
                    // Try display name as last resort
                    for (Status s : values()) {
                        if (s. getDisplayName().equalsIgnoreCase(value)) {
                            return s;
                        }
                    }
                    return null;
            }
        }

        // Helper method to remove Vietnamese accents
        private static String removeVietnameseAccents(String str) {
            if (str == null) return "";

            return str
                    .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                    .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                    .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                    .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                    .replaceAll("[ìíịỉĩ]", "i")
                    .replaceAll("[ÌÍỊỈĨ]", "I")
                    .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                    .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                    .replaceAll("[ùúụủũưừứựửữ]", "u")
                    .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                    .replaceAll("[ỳýỵỷỹ]", "y")
                    .replaceAll("[ỲÝỴỶỸ]", "Y")
                    .replaceAll("[đ]", "d")
                    .replaceAll("[Đ]", "D");
        }
    }
}
