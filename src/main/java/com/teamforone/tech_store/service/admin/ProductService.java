package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductListDTO> getAllProducts();
    Product addProduct(ProductRequest product) throws IOException;
    Response updateProduct(String id, ProductRequest product);
    Response deleteProduct(String id);
    Product findProductById(String id);

}
