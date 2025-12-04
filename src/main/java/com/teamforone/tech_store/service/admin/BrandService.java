package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.BrandRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.enums.BrandStatus;
import com.teamforone.tech_store.model.Brands;

import java.io.IOException;
import java.util.List;

public interface BrandService {
    List<Brands> getAllBrands();

    List<Brands> getActiveBrands();

    List<Brands> findByStatus(BrandStatus status);

    Brands findBrandById(String brandID);

    Brands addBrand(BrandRequest request) throws IOException;

    Brands updateBrand(String brandID, BrandRequest request) throws IOException;

    void deleteBrand(String brandID); // Hard delete

    void softDeleteBrand(String brandID);

    void activateBrand(String brandID);

    long countTotalBrands();

    long countActiveBrands();

    long countInactiveBrands();
}
