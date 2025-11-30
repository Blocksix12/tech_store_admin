package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
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
    public Product addProduct(ProductRequest product) throws IOException {
        String name = product.getName();
        String slug = product.getSlug();
        String description = product.getDescription();
        String brand = product.getBrandId();
        String category = product.getCategoryId();
        String imageUrl = null;
        if (product.getDefaultImage() != null && !product.getDefaultImage().isEmpty()) {
            imageUrl = fileStorageService.saveFile(product.getDefaultImage());
        }
        String status = product.getStatus();

        Product newProduct = Product.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .brandId(brand)
                .categoryId(category)
                .imageUrl(imageUrl)
                .productStatus(Product.Status.toEnum(status))
                .build();
        return productRepository.save(newProduct);
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


}
