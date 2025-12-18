package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.*;
import com.teamforone.tech_store.model.CTProducts;
import com.teamforone.tech_store.repository.admin.crud.*;
import com.teamforone.tech_store.service.admin.CTProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CTProductServiceImpl implements CTProductService {
    private final ColorRepository colorRepository;
    private final StorageRepository storageRepository;
    private final DisplaySizeRepository displaySizeRepository;
    private final ProductRepository productRepository;
    // Add your CTProduct repository here
     private final CTProductRepository ctProductRepository;

    /**
     * Lấy tất cả màu sắc
     */
    public List<ColorDTO> getAllColors() {
        return colorRepository.findAllByOrderByColorNameAsc().stream()
                .map(color -> ColorDTO.builder()
                        .id(color.getColorID())
                        .name(color. getColorName())
                        .build())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả dung lượng (Storage)
     */
    public List<StorageDTO> getAllStorages() {
        return storageRepository.findAllOrderByRom(). stream()
                .map(storage -> StorageDTO.builder()
                        .id(storage.getStorageID())
                        . name(storage.getRam() + "/" + storage.getRom()) // "8GB/128GB"
                        .ram(storage.getRam())
                        .rom(storage. getRom())
                        .build())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả kích thước màn hình
     */
    public List<DisplaySizeDTO> getAllSizes() {
        return displaySizeRepository.findAllByOrderBySizeInchAsc().stream()
                .map(size -> {
                    // Tạo tên hiển thị đẹp: "6.7\" FHD+ 120Hz"
                    StringBuilder displayName = new StringBuilder();
                    displayName.append(size.getSizeInch()).append("\"");

                    if (size.getResolution() != null) {
                        displayName.append(" ").append(size.getResolution());
                    }

                    if (size.getRefreshRate() != null) {
                        displayName.append(" ").append(size.getRefreshRate());
                    }

                    return DisplaySizeDTO.builder()
                            .id(size.getDisplaySizeID())
                            .name(displayName.toString())
                            .sizeInch(size.getSizeInch())
                            .resolution(size.getResolution())
                            .technology(size.getTechnology())
                            .refreshRate(size.getRefreshRate())
                            .build();
                })
                .distinct()
                .collect(Collectors.toList());
    }
    @Override
    public List<CTProductRequest> getAllProduct() {
        List<CTProducts> results = ctProductRepository.findAll();

        return results.stream()
                .map(this::CTProductRequest)
                .collect(Collectors.toList());
    }

    private CTProductRequest CTProductRequest(CTProducts ct) {
        CTProductRequest dto = new CTProductRequest();

        // ---- Composite Key Field ----
        dto.setProductId(ct.getProductId());
        dto.setColorId(ct.getColorId());
        dto.setStorageId(ct.getStorageId());
        dto.setSizeId(ct.getSizeId());

        // ---- Normal Fields ----
        dto.setPrice(ct.getPrice());
        dto.setSalePrice(ct.getSalePrice());
        dto.setQuantity(ct.getQuantity());

        // ---- Optional: Relationship Display ----
        if (ct.getProduct() != null) {
            dto.setProductName(ct.getProduct().getName());
            dto.setImageUrl(ct.getProduct().getImageUrl());
        }

        if (ct.getColor() != null) {
            dto.setColorName(ct.getColor().getColorName());
        }

        if (ct.getStorage() != null) {
            dto.setRomName(ct.getStorage().getRom());
        }

        if (ct.getSize() != null) {
            dto.setSizeName(ct.getSize().getSizeInch().toString());
        }

        return dto;
    }

    @Override
    @Transactional
    public void addProductVariant(CTProductRequest request) {
        // Kiểm tra biến thể đã tồn tại
        if (isVariantExists(request.getProductId(), request.getColorId(),
                request.getStorageId(), request.getSizeId())) {
            throw new IllegalArgumentException("Biến thể này đã tồn tại!");
        }

        // Tạo entity mới
        CTProducts ctProduct = new CTProducts();

        // Set composite key
        ctProduct.setProductId(request.getProductId());
        ctProduct.setColorId(request.getColorId());
        ctProduct.setStorageId(request.getStorageId());
        ctProduct.setSizeId(request.getSizeId());

        // Set các trường thông tin
        ctProduct.setPrice(request.getPrice());
        ctProduct.setSalePrice(request.getSalePrice());
        ctProduct.setQuantity(request.getQuantity());

        // Set relationships (optional - nếu cần load full object)
        productRepository.findById(request.getProductId())
                .ifPresent(ctProduct::setProduct);

        colorRepository.findById(request.getColorId())
                .ifPresent(ctProduct::setColor);

        if (request.getStorageId() != null) {
            storageRepository.findById(request.getStorageId())
                    .ifPresent(ctProduct::setStorage);
        }

        if (request.getSizeId() != null) {
            displaySizeRepository.findById(request.getSizeId())
                    .ifPresent(ctProduct::setSize);
        }

        // Lưu vào database
        ctProductRepository.save(ctProduct);
    }

    @Override
    public boolean isVariantExists(String productId, String colorId,
                                   String storageId, String sizeId) {
        return ctProductRepository.findByCompositeKey(
                productId, colorId, storageId, sizeId
        ).isPresent();
    }
}
