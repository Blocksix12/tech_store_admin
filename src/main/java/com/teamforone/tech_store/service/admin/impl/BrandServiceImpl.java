package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.BrandRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.enums.BrandStatus;
import com.teamforone.tech_store.model.Brands;
import com.teamforone.tech_store.repository.admin.crud.BrandRepository;
import com.teamforone.tech_store.service.admin.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private final BrandRepository brandRepository;
    private final FileStorageService fileStorageService;

    public BrandServiceImpl(BrandRepository brandRepository,
                            FileStorageService fileStorageService) {
        this.brandRepository = brandRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brands> getActiveBrands() {
        return brandRepository.findByStatus(BrandStatus.ACTIVE);
    }

    @Override
    public List<Brands> findByStatus(BrandStatus status) {
        return brandRepository.findByStatus(status);
    }

    @Override
    public Brands findBrandById(String brandID) {
        return brandRepository. findById(brandID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + brandID));
    }

    @Override
    public Brands addBrand(BrandRequest request) throws IOException {
        validateBrandRequest(request);

        String brandName = request.getBrandName().trim();
        if (brandRepository.existsByBrandNameAndStatus(brandName, BrandStatus.ACTIVE)) {
            throw new IllegalArgumentException("Thương hiệu với tên '" + brandName + "' đã tồn tại.");
        }

        String brandStatus = request.getStatus() != null ? request.getStatus().name() : BrandStatus.ACTIVE.name();
        String description = request.getDescription();
        String logoUrl = null;
        if (request.getDefaultImage() != null && ! request.getDefaultImage().isEmpty()) {
            logoUrl = fileStorageService.saveFile(request.getDefaultImage());
        }

        String websiteUrl = request.getWebsiteUrl();
        String country = request.getCountry();
        Integer displayOrder = request.getDisplayOrder() != null ? request.getDisplayOrder() : 0;

        Brands brand = Brands.builder()
                .brandName(brandName)
                .status(BrandStatus.valueOf(brandStatus))
                .description(description)
                .logoUrl(logoUrl)
                .websiteUrl(websiteUrl)
                .country(country)
                .displayOrder(displayOrder)
                .build();

        return brandRepository.save(brand);
    }

    @Override
    public Brands updateBrand(String brandID, BrandRequest request) throws IOException {
        validateBrandRequest(request);

        Brands brand = findBrandById(brandID);

        if(brand == null) {
            throw new RuntimeException("Không tìm thấy thương hiệu với ID: " + brandID);
        }

        String brandName = request.getBrandName(). trim();
        String brandStatus = request.getStatus() != null ? request.getStatus(). name() : BrandStatus.ACTIVE.name();
        String description = request.getDescription();

        // ✅ Handle image update
        MultipartFile newImage = request.getDefaultImage();
        if (newImage != null && !newImage.isEmpty()) {
            String oldImageUrl = brand.getLogoUrl(); // ✅ Lấy từ brand entity, không phải request

            // Upload ảnh mới
            String newImageUrl = fileStorageService.saveFile(newImage);
            brand.setLogoUrl(newImageUrl);

            // Xóa ảnh cũ (nếu tồn tại)
            if (oldImageUrl != null && ! oldImageUrl.isEmpty()) {
                try {
                    fileStorageService. deleteFile(oldImageUrl);
                } catch (Exception e) {
                    System.err.println("Không thể xóa ảnh cũ: " + e. getMessage());
                }
            }
        }
        // ✅ If no new image uploaded, keep the old one (don't change logoUrl)

        String websiteUrl = request.getWebsiteUrl();
        String country = request.getCountry();
        Integer displayOrder = request.getDisplayOrder() != null ? request.getDisplayOrder() : 0;

        brand.setBrandName(brandName);
        brand.setStatus(BrandStatus.valueOf(brandStatus));
        brand.setDescription(description);
        brand.setWebsiteUrl(websiteUrl);
        brand.setCountry(country);
        brand.setDisplayOrder(displayOrder);

        return brandRepository.save(brand);
    }

    @Override
    public void deleteBrand(String brandID) {
        Brands brand = findBrandById(brandID);
        brandRepository.delete(brand);
    }

    @Override
    public void softDeleteBrand(String brandID) {
        // Soft delete - just change status
        Brands brand = findBrandById(brandID);
        brand.setStatus(BrandStatus.INACTIVE);
        brandRepository. save(brand);
    }

    @Override
    public void activateBrand(String brandID) {
        Brands brand = findBrandById(brandID);
        brand.setStatus(BrandStatus.ACTIVE);
        brandRepository.save(brand);
    }

    @Override
    public long countTotalBrands() {
        return brandRepository.count();
    }

    @Override
    public long countActiveBrands() {
        return brandRepository.countByStatus(BrandStatus.ACTIVE);
    }

    @Override
    public long countInactiveBrands() {
        return brandRepository.countByStatus(BrandStatus.INACTIVE);
    }

    @Override
    public Optional<Brands> findByBrandName(String brandName) {
        return brandRepository.findByBrandName(brandName);
    }
    private void validateBrandRequest(BrandRequest request) {
        if (request.getBrandName() == null || request.getBrandName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống");
        }
    }
}
