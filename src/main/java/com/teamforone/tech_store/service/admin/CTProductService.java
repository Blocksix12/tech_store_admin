package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.*;

import java.util.List;

public interface CTProductService {
    List<CTProductRequest> getAllProduct();

    void addProductVariant(CTProductRequest request);
    boolean isVariantExists(String productId, String colorId,
                            String storageId, String sizeId);

    // Dropdown data
//    List<ProductRequest> getAllProducts();

    List<ColorDTO> getAllColors();

    List<StorageDTO> getAllStorages();

    List<DisplaySizeDTO> getAllSizes();
}
