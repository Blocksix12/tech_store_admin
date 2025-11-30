package com.teamforone.tech_store.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String id;
    private String name;
    private String slug;
    private String description;
    private String brandId;
    private String categoryId;
    private String imageUrl;
    private String status;
    private MultipartFile defaultImage;
    private String createdAt;
    private String updatedAt;
}
