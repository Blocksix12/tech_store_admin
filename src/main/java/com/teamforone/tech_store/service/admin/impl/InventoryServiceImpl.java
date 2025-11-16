package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.InventoryRequest;
import com.teamforone.tech_store.dto.response.InventoryResponse;
import com.teamforone.tech_store.dto.response.InventoryStatistics;
import com.teamforone.tech_store.dto.response.ProductInventorySummary;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.CTProductId;
import com.teamforone.tech_store.model.CTProducts;
import com.teamforone.tech_store.repository.admin.crud.CTProductRepository;
import com.teamforone.tech_store.service.admin.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private CTProductRepository ctProductRepository;

    // ========== CÁC CHỨC NĂNG CŨ ==========

    @Override
    public InventoryResponse getInventory(String productId, String colorId, String storageId, String sizeId) {
        CTProductId id = new CTProductId(productId, colorId, storageId, sizeId);
        CTProducts variant = ctProductRepository.findById(id).orElse(null);

        if (variant == null) {
            return null;
        }

        return mapToInventoryResponse(variant);
    }

    @Override
    public List<InventoryResponse> getProductInventory(String productId) {
        List<CTProducts> variants = ctProductRepository.findByProductId(productId);
        return variants.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getLowStockProducts(Integer threshold) {
        List<CTProducts> lowStockVariants = ctProductRepository.findByQuantityLessThanEqual(threshold);
        return lowStockVariants.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getOutOfStockProducts() {
        List<CTProducts> outOfStockVariants = ctProductRepository.findByQuantity(0);
        return outOfStockVariants.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Response addStock(InventoryRequest request) {
        CTProductId id = new CTProductId(
                request.getProductId(),
                request.getColorId(),
                request.getStorageId(),
                request.getSizeId()
        );

        CTProducts variant = ctProductRepository.findById(id).orElse(null);

        if (variant == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product variant not found")
                    .build();
        }

        if (request.getQuantity() <= 0) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Quantity must be greater than 0")
                    .build();
        }

        variant.setQuantity(variant.getQuantity() + request.getQuantity());
        ctProductRepository.save(variant);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Stock added successfully. New quantity: " + variant.getQuantity())
                .build();
    }

    @Override
    @Transactional
    public Response reduceStock(InventoryRequest request) {
        CTProductId id = new CTProductId(
                request.getProductId(),
                request.getColorId(),
                request.getStorageId(),
                request.getSizeId()
        );

        CTProducts variant = ctProductRepository.findById(id).orElse(null);

        if (variant == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product variant not found")
                    .build();
        }

        if (request.getQuantity() <= 0) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Quantity must be greater than 0")
                    .build();
        }

        if (variant.getQuantity() < request.getQuantity()) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Insufficient stock. Available: " + variant.getQuantity())
                    .build();
        }

        variant.setQuantity(variant.getQuantity() - request.getQuantity());
        ctProductRepository.save(variant);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Stock reduced successfully. Remaining quantity: " + variant.getQuantity())
                .build();
    }

    @Override
    @Transactional
    public Response updateStock(InventoryRequest request) {
        CTProductId id = new CTProductId(
                request.getProductId(),
                request.getColorId(),
                request.getStorageId(),
                request.getSizeId()
        );

        CTProducts variant = ctProductRepository.findById(id).orElse(null);

        if (variant == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product variant not found")
                    .build();
        }

        if (request.getQuantity() < 0) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Quantity cannot be negative")
                    .build();
        }

        variant.setQuantity(request.getQuantity());
        ctProductRepository.save(variant);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Stock updated successfully. New quantity: " + variant.getQuantity())
                .build();
    }

    @Override
    public boolean checkStockAvailability(String productId, String colorId, String storageId, String sizeId, Integer quantity) {
        CTProductId id = new CTProductId(productId, colorId, storageId, sizeId);
        CTProducts variant = ctProductRepository.findById(id).orElse(null);

        if (variant == null) {
            return false;
        }

        return variant.getQuantity() >= quantity;
    }


    // ========== CHỨC NĂNG MỚI - BATCH OPERATIONS ==========

    @Override
    @Transactional
    public Response batchUpdateStock(List<InventoryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Request list cannot be empty")
                    .build();
        }

        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();

        for (InventoryRequest request : requests) {
            try {
                CTProductId id = new CTProductId(
                        request.getProductId(),
                        request.getColorId(),
                        request.getStorageId(),
                        request.getSizeId()
                );

                CTProducts variant = ctProductRepository.findById(id).orElse(null);

                if (variant == null) {
                    failedList.add("Product variant not found: " + request.getProductId());
                    continue;
                }

                if (request.getQuantity() < 0) {
                    failedList.add("Negative quantity for: " + request.getProductId());
                    continue;
                }

                variant.setQuantity(request.getQuantity());
                ctProductRepository.save(variant);
                successList.add(request.getProductId());

            } catch (Exception e) {
                failedList.add("Error with: " + request.getProductId() + " - " + e.getMessage());
            }
        }

        String message = String.format("Success: %d, Failed: %d",
                successList.size(), failedList.size());

        if (!failedList.isEmpty()) {
            message += ". Errors: " + String.join("; ", failedList);
        }

        return Response.builder()
                .status(failedList.isEmpty() ? HttpStatus.OK.value() : HttpStatus.PARTIAL_CONTENT.value())
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public Response batchAddStock(List<InventoryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Request list cannot be empty")
                    .build();
        }

        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();

        for (InventoryRequest request : requests) {
            try {
                Response response = addStock(request);
                if (response.getStatus() == HttpStatus.OK.value()) {
                    successList.add(request.getProductId());
                } else {
                    failedList.add(request.getProductId() + ": " + response.getMessage());
                }
            } catch (Exception e) {
                failedList.add(request.getProductId() + ": " + e.getMessage());
            }
        }

        String message = String.format("Added stock to %d variants. Failed: %d",
                successList.size(), failedList.size());

        if (!failedList.isEmpty()) {
            message += ". Errors: " + String.join("; ", failedList);
        }

        return Response.builder()
                .status(failedList.isEmpty() ? HttpStatus.OK.value() : HttpStatus.PARTIAL_CONTENT.value())
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public Response batchReduceStock(List<InventoryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Request list cannot be empty")
                    .build();
        }

        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();

        for (InventoryRequest request : requests) {
            try {
                Response response = reduceStock(request);
                if (response.getStatus() == HttpStatus.OK.value()) {
                    successList.add(request.getProductId());
                } else {
                    failedList.add(request.getProductId() + ": " + response.getMessage());
                }
            } catch (Exception e) {
                failedList.add(request.getProductId() + ": " + e.getMessage());
            }
        }

        String message = String.format("Reduced stock from %d variants. Failed: %d",
                successList.size(), failedList.size());

        if (!failedList.isEmpty()) {
            message += ". Errors: " + String.join("; ", failedList);
        }

        return Response.builder()
                .status(failedList.isEmpty() ? HttpStatus.OK.value() : HttpStatus.PARTIAL_CONTENT.value())
                .message(message)
                .build();
    }


    // ========== CHỨC NĂNG MỚI - STATISTICS & REPORTS ==========

    @Override
    public InventoryStatistics getInventoryStatistics() {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        long totalVariants = allVariants.size();
        long totalProducts = allVariants.stream()
                .map(CTProducts::getProductId)
                .distinct()
                .count();

        int totalQuantity = allVariants.stream()
                .mapToInt(CTProducts::getQuantity)
                .sum();

        double totalValue = allVariants.stream()
                .mapToDouble(v -> v.getQuantity() * v.getPrice())
                .sum();

        long outOfStock = allVariants.stream()
                .filter(v -> v.getQuantity() == 0)
                .count();

        long lowStock = allVariants.stream()
                .filter(v -> v.getQuantity() > 0 && v.getQuantity() <= 10)
                .count();

        long inStock = allVariants.stream()
                .filter(v -> v.getQuantity() > 10)
                .count();

        return InventoryStatistics.builder()
                .totalVariants(totalVariants)
                .totalProducts(totalProducts)
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .outOfStockCount(outOfStock)
                .lowStockCount(lowStock)
                .inStockCount(inStock)
                .build();
    }

    @Override
    public ProductInventorySummary getProductInventorySummary(String productId) {
        List<CTProducts> variants = ctProductRepository.findByProductId(productId);

        if (variants.isEmpty()) {
            return null;
        }

        Integer totalQuantity = variants.stream()
                .mapToInt(CTProducts::getQuantity)
                .sum();

        Double totalValue = variants.stream()
                .mapToDouble(v -> v.getQuantity() * v.getPrice())
                .sum();

        String overallStatus;
        if (totalQuantity == 0) {
            overallStatus = "OUT_OF_STOCK";
        } else if (totalQuantity <= 20) {
            overallStatus = "LOW_STOCK";
        } else {
            overallStatus = "IN_STOCK";
        }

        List<InventoryResponse> variantResponses = variants.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());

        return ProductInventorySummary.builder()
                .productId(productId)
                .productName(variants.get(0).getProduct() != null ?
                        variants.get(0).getProduct().getName() : null)
                .totalQuantity(totalQuantity)
                .totalVariants(variants.size())
                .totalValue(totalValue)
                .overallStatus(overallStatus)
                .variants(variantResponses)
                .build();
    }

    @Override
    public List<ProductInventorySummary> getAllProductInventorySummary() {
        List<String> productIds = ctProductRepository.findAll().stream()
                .map(CTProducts::getProductId)
                .distinct()
                .collect(Collectors.toList());

        return productIds.stream()
                .map(this::getProductInventorySummary)
                .filter(summary -> summary != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getTopStockProducts(Integer limit) {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        return allVariants.stream()
                .sorted((v1, v2) -> v2.getQuantity().compareTo(v1.getQuantity()))
                .limit(limit)
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getTotalInventoryValue() {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        return allVariants.stream()
                .mapToDouble(v -> v.getQuantity() * v.getPrice())
                .sum();
    }


    // ========== CHỨC NĂNG MỚI - SEARCH & FILTER ==========

    @Override
    public List<InventoryResponse> searchInventory(String productName, String colorName,
                                                   String storageName, String stockStatus) {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        return allVariants.stream()
                .filter(v -> productName == null ||
                        (v.getProduct() != null &&
                                v.getProduct().getName().toLowerCase().contains(productName.toLowerCase())))
                .filter(v -> colorName == null ||
                        (v.getColor() != null &&
                                v.getColor().getColorName().toLowerCase().contains(colorName.toLowerCase())))
                .filter(v -> stockStatus == null ||
                        getStockStatus(v.getQuantity()).equals(stockStatus))
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> filterByPriceRange(Double minPrice, Double maxPrice) {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        return allVariants.stream()
                .filter(v -> (minPrice == null || v.getPrice() >= minPrice) &&
                        (maxPrice == null || v.getPrice() <= maxPrice))
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> filterByQuantityRange(Integer minQty, Integer maxQty) {
        List<CTProducts> allVariants = ctProductRepository.findAll();

        return allVariants.stream()
                .filter(v -> (minQty == null || v.getQuantity() >= minQty) &&
                        (maxQty == null || v.getQuantity() <= maxQty))
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }


    // ========== HELPER METHODS ==========

    private InventoryResponse mapToInventoryResponse(CTProducts variant) {
        // Xây dựng storage name từ RAM + ROM
        String storageName = null;
        if (variant.getStorage() != null) {
            storageName = variant.getStorage().getRam() + " / " + variant.getStorage().getRom();
        }

        // Xây dựng size name từ size_inch
        String sizeName = null;
        if (variant.getSize() != null) {
            sizeName = variant.getSize().getSizeInch() + " inch";
        }

        return InventoryResponse.builder()
                .productId(variant.getProductId())
                .productName(variant.getProduct() != null ? variant.getProduct().getName() : null)
                .colorId(variant.getColorId())
                .colorName(variant.getColor() != null ? variant.getColor().getColorName() : null)
                .storageId(variant.getStorageId())
                .storageName(storageName)
                .sizeId(variant.getSizeId())
                .sizeName(sizeName)
                .quantity(variant.getQuantity())
                .price(variant.getPrice())
                .salePrice(variant.getSalePrice())
                .stockStatus(getStockStatus(variant.getQuantity()))
                .build();
    }

    private String getStockStatus(Integer quantity) {
        if (quantity == 0) {
            return "OUT_OF_STOCK";
        } else if (quantity <= 10) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
}