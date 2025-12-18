package com.teamforone.tech_store.dto.request;

import com.teamforone.tech_store.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandRequest {
    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(min = 2, max = 100, message = "Tên thương hiệu phải từ 2-100 ký tự")
    private String brandName;

    private BrandStatus status;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;

    private String logoUrl;

    private MultipartFile defaultImage;

    @Size(max = 255, message = "URL website không được quá 255 ký tự")
    private String websiteUrl;

    @Size(max = 100, message = "Tên quốc gia không được quá 100 ký tự")
    private String country;

    private Integer displayOrder;
}
