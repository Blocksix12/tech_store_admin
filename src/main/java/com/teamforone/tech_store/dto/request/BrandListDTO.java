package com.teamforone.tech_store.dto.request;
import com.teamforone.tech_store.enums.BrandStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandListDTO {
    private String brandId;
    private String brandName;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String country;
    private BrandStatus status;
    private Integer displayOrder;

    // ✅ Stats
    private Long productCount;
    private Double totalRevenue;
    private String formattedRevenue;

    // Timestamps
    private String createdAt;
    private String lastModifiedAt;
}
