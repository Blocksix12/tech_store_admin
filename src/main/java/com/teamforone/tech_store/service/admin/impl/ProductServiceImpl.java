package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.repository.admin.crud.ProductRepository;
import com.teamforone.tech_store.service.admin.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
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
    public Response updateProduct(String id, ProductRequest product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product not found")
                    .build();
        }
        existingProduct.setName(product.getName());
        existingProduct.setSlug(product.getSlug());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBrandId(product.getBrandId());
        existingProduct.setCategoryId(product.getCategoryId());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setProductStatus(Product.Status.toEnum(product.getStatus()));

        productRepository.save(existingProduct);
        // Implementation for updating a product would go here
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product updated successfully")
                .build();
    }

    @Override
    public Response deleteProduct(String id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Product not found")
                    .build();
        }

        productRepository.delete(existingProduct);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Product deleted successfully")
                .build();
    }

    @Override
    public Product findProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }


}
