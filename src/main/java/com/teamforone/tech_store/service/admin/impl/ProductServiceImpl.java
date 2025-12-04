package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.CTProductRequest;
import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.CTProducts;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.repository.admin.crud.CTProductRepository;
import com.teamforone.tech_store.repository.admin.crud.ProductRepository;
import com.teamforone.tech_store.service.admin.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final CTProductRepository ctProductRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CTProductRepository ctProductRepository,
                              FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.ctProductRepository = ctProductRepository;
        this.fileStorageService = fileStorageService;
    }
    @Override
    public List<ProductListDTO> getAllProducts() {
        List<Object[]> results = productRepository.findAllProducts();

        return results.stream()
                .map(this::mapToProductListDTO)
                .collect(Collectors.toList());
    }

    private ProductListDTO mapToProductListDTO(Object[] row) {
        String id = (String) row[0];
        String name = (String) row[1];
        String slug = (String) row[2];
        String categoryName = (String) row[3];
        Double minPrice = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
        Double maxPrice = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
        Long totalQuantity = row[6] != null ? ((Number) row[6]).longValue() : 0L;
        String statusStr = (String) row[7];
        String imageUrl = (String) row[8];
        String brands = (String) row[9];

        Product.Status status = Product.Status.toEnum(statusStr);

        return new ProductListDTO(id, name, slug, categoryName, minPrice, maxPrice,
                totalQuantity, status, imageUrl, brands);
    }

    @Override
    @Transactional
    public Product addProduct(ProductRequest product) throws IOException {
        String name = product.getName();
        String slug = product.getSlug();
        String description = product.getDescription();
        String brand = product.getBrandId();
        String category = product.getCategoryId();
        String imageUrl = null;

        if (product.getDefaultImage() != null && ! product.getDefaultImage().isEmpty()) {
            imageUrl = fileStorageService.saveFile(product.getDefaultImage());
        }
        String status = product.getStatus();

        // ✅ Bước 1: Tạo và lưu Product trước
        Product newProduct = Product.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .brandId(brand)
                .categoryId(category)
                .imageUrl(imageUrl)
                .productStatus(Product.Status.toEnum(status))
                .build();

        Product savedProduct = productRepository.save(newProduct);

        // ✅ Bước 2: Nếu có danh sách biến thể, lưu từng biến thể
        if (product.getCtProducts() != null && !product.getCtProducts().isEmpty()) {
            for (CTProductRequest variantRequest : product.getCtProducts()) {
                // Validate biến thể
                if (variantRequest.getColorId() == null || variantRequest.getColorId().isEmpty()) {
                    throw new IllegalArgumentException("Màu sắc không được để trống!");
                }

                if (variantRequest.getPrice() == null || variantRequest. getPrice() <= 0) {
                    throw new IllegalArgumentException("Giá phải lớn hơn 0!");
                }

                if (variantRequest.getQuantity() == null || variantRequest.getQuantity() < 0) {
                    throw new IllegalArgumentException("Số lượng không được âm!");
                }

                // Validate giá khuyến mãi
                if (variantRequest.getSalePrice() != null &&
                        variantRequest.getSalePrice() > 0 &&
                        variantRequest.getSalePrice() >= variantRequest.getPrice()) {
                    throw new IllegalArgumentException(
                            "Giá khuyến mãi phải nhỏ hơn giá gốc!");
                }

                // Tạo CTProduct entity
                CTProducts ctProduct = new CTProducts();
                ctProduct. setProductId(savedProduct.getId());  // ✅ Sử dụng ID từ product đã lưu
                ctProduct.setColorId(variantRequest.getColorId());
                ctProduct.setStorageId(variantRequest.getStorageId());
                ctProduct.setSizeId(variantRequest.getSizeId());
                ctProduct.setPrice(variantRequest.getPrice());
                ctProduct.setSalePrice(variantRequest.getSalePrice());
                ctProduct.setQuantity(variantRequest. getQuantity());

                // Lưu biến thể
                ctProductRepository.save(ctProduct);
            }
        }

        return savedProduct;
    }

    @Override
    public Product updateProduct(String id,ProductRequest product) throws IOException {
        Product existingProduct = productRepository.findById(id).orElse(null);
        String name = product.getName();
        String slug = product.getSlug();
        String description = product.getDescription();
        String brand = product.getBrandId();
        String category = product.getCategoryId();
        MultipartFile newImage = product.getDefaultImage();
        if (newImage != null && !newImage.isEmpty()) {
            String oldImageUrl = product.getImageUrl();

            // Upload ảnh mới
            String newImageUrl = fileStorageService.saveFile(newImage);
            existingProduct.setImageUrl(newImageUrl);

            // Xóa ảnh cũ (nếu tồn tại và khác ảnh mặc định)
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                try {
                    fileStorageService.deleteFile(oldImageUrl);
                } catch (Exception e) {
                    // Log nhưng không throw exception
                    System.err.println("Không thể xóa ảnh cũ: " + e.getMessage());
                }
            }
        }
        String status = product.getStatus();

        if (existingProduct == null){
            throw new IOException("Product not found");
        }
        existingProduct.setName(name);
        existingProduct.setSlug(slug);
        existingProduct.setDescription(description);
        existingProduct.setBrandId(brand);
        existingProduct.setCategoryId(category);
        existingProduct.setProductStatus(Product.Status.toEnum(status));

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public Product deleteProduct(String id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return null;
        }

        try {
            ctProductRepository.deleteByProductId(id);

            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                try {
                    fileStorageService.deleteFile(existingProduct.getImageUrl());
                } catch (Exception e) {
                    System.err.println("Không thể xóa ảnh sản phẩm: " + e.getMessage());
                }
            }

            // Bước 3: Xóa sản phẩm
            productRepository.delete(existingProduct);

            return existingProduct;

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
            throw new RuntimeException("Không thể xóa sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public Product findProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }


    @Override
    public List<Product.Status> getAllProductStatuses() {
        return List.of(Product.Status.values());
    }

}
