package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.CTProductRequest;
import com.teamforone.tech_store.model.CTProducts;
import com.teamforone.tech_store.repository.admin.crud.CTProductRepository;
import com.teamforone.tech_store.service.admin.CTProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CTProductServiceImpl implements CTProductService {
    private final CTProductRepository ctProductRepository;

    public CTProductServiceImpl(CTProductRepository ctProductRepository) {
        this.ctProductRepository = ctProductRepository;
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
            dto.setRamName(ct.getStorage().getRam());
        }

        if (ct.getStorage() != null) {
            dto.setRomName(ct.getStorage().getRom());
        }

        if (ct.getSize() != null) {
            dto.setSizeName(ct.getSize().getSizeInch().toString());
        }

        return dto;
    }
}
