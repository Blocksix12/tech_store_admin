package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.service.admin.BrandService;
import com.teamforone.tech_store.service.admin.CategoryService;
import com.teamforone.tech_store.service.admin.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductController {
    @Autowired
    private final ProductService productService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final BrandService brandService;

    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @GetMapping("/products")
    public String getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Lấy danh sách sản phẩm
        List<ProductListDTO> allProducts = productService.getAllProducts();

        // Tính toán pagination
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / size);

        // Đảm bảo page hợp lệ
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        // Lấy sản phẩm cho trang hiện tại
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalProducts);

        List<ProductListDTO> products = allProducts.subList(
                Math.min(startIndex, totalProducts),
                endIndex
        );

        // Thêm attributes vào model
        model.addAttribute("products", products);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("startIndex", startIndex + 1);
        model.addAttribute("endIndex", endIndex);

        return "ProductList";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        // Populate categories and brands for dropdown
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        return "AddProducts";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductRequest productRequest,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "AddProducts";
        }

        try {
            productService.addProduct(productRequest);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm thành công!");
            return "redirect:/admin/products";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload file: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "AddProducts";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "AddProducts";
        }
    }

    @PutMapping("/products/update/{id}")
    public ResponseEntity<Response> updateProduct(@PathVariable String id , @RequestBody ProductRequest productRequest) {
        Response response = productService.updateProduct(id, productRequest);
        // Implementation for adding a product would go here
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable String id) {
        Response response = productService.deleteProduct(id);
        // Implementation for deleting a product would go here
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/products/{id}")
    public Product findProductById(@PathVariable String id){
        return productService.findProductById(id);
    }

}
