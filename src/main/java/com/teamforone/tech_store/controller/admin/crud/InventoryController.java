package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.BatchInventoryRequest;
import com.teamforone.tech_store.dto.request.InventoryRequest;
import com.teamforone.tech_store.dto.response.InventoryResponse;
import com.teamforone.tech_store.dto.response.InventoryStatistics;
import com.teamforone.tech_store.dto.response.ProductInventorySummary;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.service.admin.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ========== CÁC ENDPOINT CŨ ==========

    // Lấy thông tin tồn kho của 1 variant cụ thể
    @GetMapping("/variant/{productId}/{colorId}/{sizeId}/{storageId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable String productId,
            @PathVariable String colorId,
            @PathVariable String sizeId,
            @PathVariable String storageId) {

        InventoryResponse response = inventoryService.getInventory(
                productId, colorId, sizeId, storageId);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(response);
    }

    // Lấy tất cả variants của 1 product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getProductInventory(@PathVariable String productId) {
        List<InventoryResponse> responses = inventoryService.getProductInventory(productId);
        return ResponseEntity.ok(responses);
    }

    // Lấy danh sách sản phẩm sắp hết hàng
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<InventoryResponse> responses = inventoryService.getLowStockProducts(threshold);
        return ResponseEntity.ok(responses);
    }

    // Lấy danh sách sản phẩm hết hàng
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryResponse>> getOutOfStockProducts() {
        List<InventoryResponse> responses = inventoryService.getOutOfStockProducts();
        return ResponseEntity.ok(responses);
    }

    // Nhập hàng (thêm tồn kho)
    @PostMapping("/add")
    public ResponseEntity<Response> addStock(@RequestBody InventoryRequest request) {
        Response response = inventoryService.addStock(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Xuất hàng (giảm tồn kho)
    @PostMapping("/reduce")
    public ResponseEntity<Response> reduceStock(@RequestBody InventoryRequest request) {
        Response response = inventoryService.reduceStock(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Cập nhật tồn kho trực tiếp
    @PutMapping("/update")
    public ResponseEntity<Response> updateStock(@RequestBody InventoryRequest request) {
        Response response = inventoryService.updateStock(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Kiểm tra tồn kho có đủ không
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkStockAvailability(
            @RequestParam String productId,
            @RequestParam String colorId,
            @RequestParam String storageId,
            @RequestParam String sizeId,
            @RequestParam Integer quantity) {

        boolean available = inventoryService.checkStockAvailability(
                productId, colorId, storageId, sizeId, quantity);

        return ResponseEntity.ok(available);
    }


    // ========== ENDPOINT MỚI - BATCH OPERATIONS ==========

    // Cập nhật nhiều variant cùng lúc
    @PutMapping("/batch-update")
    public ResponseEntity<Response> batchUpdateStock(@RequestBody BatchInventoryRequest request) {
        Response response = inventoryService.batchUpdateStock(request.getItems());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Nhập hàng nhiều variant
    @PostMapping("/batch-add")
    public ResponseEntity<Response> batchAddStock(@RequestBody BatchInventoryRequest request) {
        Response response = inventoryService.batchAddStock(request.getItems());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Xuất hàng nhiều variant
    @PostMapping("/batch-reduce")
    public ResponseEntity<Response> batchReduceStock(@RequestBody BatchInventoryRequest request) {
        Response response = inventoryService.batchReduceStock(request.getItems());
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    // ========== ENDPOINT MỚI - STATISTICS & REPORTS ==========

    // Thống kê tổng quan
    @GetMapping("/statistics")
    public ResponseEntity<InventoryStatistics> getStatistics() {
        return ResponseEntity.ok(inventoryService.getInventoryStatistics());
    }

    // Tổng hợp theo product
    @GetMapping("/summary/product/{productId}")
    public ResponseEntity<ProductInventorySummary> getProductSummary(@PathVariable String productId) {
        ProductInventorySummary summary = inventoryService.getProductInventorySummary(productId);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

    // Danh sách tất cả products với tổng tồn kho
    @GetMapping("/summary/all")
    public ResponseEntity<List<ProductInventorySummary>> getAllProductSummary() {
        return ResponseEntity.ok(inventoryService.getAllProductInventorySummary());
    }

    // Top sản phẩm tồn kho cao nhất
    @GetMapping("/top-stock")
    public ResponseEntity<List<InventoryResponse>> getTopStock(
            @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(inventoryService.getTopStockProducts(limit));
    }

    // Tổng giá trị tồn kho
    @GetMapping("/total-value")
    public ResponseEntity<Double> getTotalValue() {
        return ResponseEntity.ok(inventoryService.getTotalInventoryValue());
    }


    // ========== ENDPOINT MỚI - SEARCH & FILTER ==========

    // Tìm kiếm nâng cao
    @GetMapping("/search")
    public ResponseEntity<List<InventoryResponse>> searchInventory(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String colorName,
            @RequestParam(required = false) String storageName,
            @RequestParam(required = false) String stockStatus) {
        return ResponseEntity.ok(inventoryService.searchInventory(
                productName, colorName, storageName, stockStatus));
    }

    // Filter theo giá
    @GetMapping("/filter/price")
    public ResponseEntity<List<InventoryResponse>> filterByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(inventoryService.filterByPriceRange(minPrice, maxPrice));
    }

    // Filter theo số lượng
    @GetMapping("/filter/quantity")
    public ResponseEntity<List<InventoryResponse>> filterByQuantity(
            @RequestParam(required = false) Integer minQty,
            @RequestParam(required = false) Integer maxQty) {
        return ResponseEntity.ok(inventoryService.filterByQuantityRange(minQty, maxQty));
    }
}




// ✅ Lấy được thông tin tồn kho của 1 variant
// ✅ Lấy được tất cả variants của 1 product
// ✅ Nhập hàng thành công, quantity tăng đúng
// ✅ Xuất hàng thành công, quantity giảm đúng
// ✅ Không xuất được quá số lượng tồn kho
// ✅ Cập nhật trực tiếp số lượng thành công
// ✅ Batch add nhiều variants thành công
// ✅ Batch operations xử lý lỗi đúng (1 thành công, 1 lỗi)
// ✅ Thống kê tổng quan hiển thị đúng số liệu
// ✅ Lấy danh sách low stock đúng
// ✅ Lấy danh sách out of stock đúng
// ✅ Tìm kiếm theo tên product hoạt động
// ✅ Tìm kiếm theo stockStatus hoạt động
// ✅ Lọc theo khoảng giá hoạt động
// ✅ Lọc theo khoảng số lượng hoạt động
// ✅ Top stock hiển thị đúng thứ tự