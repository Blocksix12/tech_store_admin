package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.InventoryRequest;
import com.teamforone.tech_store.dto.response.InventoryResponse;
import com.teamforone.tech_store.dto.response.InventoryStatistics;
import com.teamforone.tech_store.dto.response.ProductInventorySummary;
import com.teamforone.tech_store.dto.response.Response;

import java.util.List;

public interface InventoryService {
    // ========== CÁC CHỨC NĂNG CŨ ==========

    // Lấy tồn kho của 1 variant cụ thể
    InventoryResponse getInventory(String productId, String colorId, String storageId, String sizeId);

    // Lấy tất cả variants của 1 product
    List<InventoryResponse> getProductInventory(String productId);

    // Lấy danh sách sản phẩm sắp hết hàng
    List<InventoryResponse> getLowStockProducts(Integer threshold);

    // Lấy danh sách sản phẩm hết hàng
    List<InventoryResponse> getOutOfStockProducts();

    // Thêm tồn kho (nhập hàng)
    Response addStock(InventoryRequest request);

    // Giảm tồn kho (xuất hàng/bán hàng)
    Response reduceStock(InventoryRequest request);

    // Cập nhật tồn kho trực tiếp
    Response updateStock(InventoryRequest request);

    // Kiểm tra tồn kho có đủ không
    boolean checkStockAvailability(String productId, String colorId, String storageId, String sizeId, Integer quantity);


    // ========== CHỨC NĂNG MỚI - BATCH OPERATIONS ==========

    // Cập nhật nhiều variant cùng lúc
    Response batchUpdateStock(List<InventoryRequest> requests);

    // Nhập hàng nhiều variant
    Response batchAddStock(List<InventoryRequest> requests);

    // Xuất hàng nhiều variant
    Response batchReduceStock(List<InventoryRequest> requests);


    // ========== CHỨC NĂNG MỚI - STATISTICS & REPORTS ==========

    // Thống kê tổng quan
    InventoryStatistics getInventoryStatistics();

    // Tổng hợp theo product
    ProductInventorySummary getProductInventorySummary(String productId);

    // Danh sách tất cả products với tổng tồn kho
    List<ProductInventorySummary> getAllProductInventorySummary();

    // Top sản phẩm tồn kho cao nhất
    List<InventoryResponse> getTopStockProducts(Integer limit);

    // Tổng giá trị tồn kho
    Double getTotalInventoryValue();


    // ========== CHỨC NĂNG MỚI - SEARCH & FILTER ==========

    // Tìm kiếm theo nhiều tiêu chí
    List<InventoryResponse> searchInventory(String productName, String colorName,
                                            String storageName, String stockStatus);

    // Filter theo khoảng giá
    List<InventoryResponse> filterByPriceRange(Double minPrice, Double maxPrice);

    // Filter theo khoảng số lượng
    List<InventoryResponse> filterByQuantityRange(Integer minQty, Integer maxQty);
}