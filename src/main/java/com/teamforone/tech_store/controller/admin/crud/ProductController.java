package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.CTProductRequest;
import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.service.admin.BrandService;
import com.teamforone.tech_store.service.admin.CTProductService;
import com.teamforone.tech_store.service.admin.CategoryService;
import com.teamforone.tech_store.service.admin.ProductService;
import com.teamforone.tech_store.service.admin.impl.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class ProductController {
    @Autowired
    private final ProductService productService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final BrandService brandService;
    @Autowired
    private final FileStorageService fileStorageService;
    @Autowired
    private CTProductService ctProductService;

    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.fileStorageService = fileStorageService;
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

        model. addAttribute("colors", ctProductService.getAllColors());
        model. addAttribute("storages", ctProductService.getAllStorages());
        model.addAttribute("sizes", ctProductService.getAllSizes());
        return "AddProducts";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductRequest productRequest,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // ✅ Repopulate tất cả dữ liệu khi có lỗi
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddProducts";
        }

        try {
            Product savedProduct = productService.addProduct(productRequest);

            redirectAttributes.addFlashAttribute("success",
                    "Sản phẩm và " +
                            (productRequest.getCtProducts() != null ?  productRequest.getCtProducts().size() : 0) +
                            " biến thể đã được thêm thành công!");

            return "redirect:/admin/products";

        } catch (IllegalArgumentException e) {
            // ✅ Lỗi validation nghiệp vụ
            model.addAttribute("error", e.getMessage());
            model. addAttribute("categories", categoryService. getAllCategories());
            model. addAttribute("brands", brandService. getAllBrands());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddProducts";

        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload file: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddProducts";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model. addAttribute("categories", categoryService. getAllCategories());
            model. addAttribute("brands", brandService. getAllBrands());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddProducts";
        }
    }

    @GetMapping("/products/update/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        try {
            // Lấy thông tin sản phẩm theo ID
            Product product = productService.findProductById(id);

            if (product == null) {
                model.addAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products";
            }

            // Chuyển đổi Product sang ProductRequest nếu cần
            ProductRequest productRequest = convertToProductRequest(product);
            productRequest.setId(product.getId());

            model.addAttribute("product", productRequest);
            model.addAttribute("defaultImage", product.getImageUrl());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("createdAt", product.getCreatedAt());
            model.addAttribute("updatedAt", product.getUpdatedAt());
            model.addAttribute("brands", brandService.getAllBrands());

            return "EditProduct";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    private ProductRequest convertToProductRequest(Product product) {
        ProductRequest request = new ProductRequest();
        request.setName(product.getName());
        request.setSlug(product.getSlug());
        request.setDescription(product.getDescription());
        request.setStatus(product.getProductStatus().toString());
        request.setCategoryId(product.getCategoryId());
        request.setBrandId(product.getBrandId());
        request.setImageUrl(product.getImageUrl());
        return request;
    }


    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable String id,
                                @Valid @ModelAttribute("product") ProductRequest productRequest,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("product", productRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "EditProduct";
        }

        try {
            productService.updateProduct(id, productRequest);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được cập nhật thành công!");
            return "redirect:/admin/products";
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi upload file: " + e.getMessage());
            model.addAttribute("product", productRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "EditProduct";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("product", productRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "EditProduct";
        }
    }

    @GetMapping("/products/delete/{id}")
    public String showDeleteConfirmation(@PathVariable String id, Model model) {
        try {
            Product product = productService.findProductById(id);

            if (product == null) {
                model.addAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products";
            }

            ProductRequest productRequest = convertToProductRequest(product);
            productRequest.setId(product.getId());

            model.addAttribute("product", productRequest);
            model.addAttribute("categoryName", categoryService.findCategoryById(product.getCategoryId()).getCategoryName());
            model.addAttribute("brandName", brandService.findBrandById(product.getBrandId()). getBrandName());
            model. addAttribute("createdAt", product.getCreatedAt());

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map. of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Sản phẩm", "url", "/admin/products"));
            breadcrumbs.add(Map.of("name", "Xóa", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Xóa Sản phẩm");

            return "DeleteProduct";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    // POST - Xử lý xóa sản phẩm
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findProductById(id);

            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products";
            }

            // Xóa ảnh
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                try {
                    fileStorageService.deleteFile(product.getImageUrl());
                } catch (Exception e) {
                    System.err.println("Không thể xóa ảnh: " + e.getMessage());
                }
            }

            // Xóa sản phẩm
            productService.deleteProduct(id);

            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa thành công!");
            return "redirect:/admin/products";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @GetMapping("/products/{id}")
    public Product findProductById(@PathVariable String id){
        return productService.findProductById(id);
    }

    @GetMapping("/products/detail/{id}")
    public String showProductDetail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findProductById(id);

            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products";
            }

            // Load category and brand separately
            String categoryName = "Chưa có";
            String brandName = "Chưa có";

            if (product.getCategoryId() != null) {
                var category = categoryService.findCategoryById(product.getCategoryId());
                if (category != null) {
                    categoryName = category.getCategoryName();
                }
            }

            if (product.getBrandId() != null) {
                var brand = brandService.findBrandById(product.getBrandId());
                if (brand != null) {
                    brandName = brand.getBrandName();
                }
            }

            // Load biến thể của sản phẩm này
            List<CTProductRequest> allVariants = ctProductService.getAllProduct();
            List<CTProductRequest> productVariants = allVariants.stream()
                    .filter(v -> v.getProductId() != null && v.getProductId().equals(id))
                    .collect(Collectors.toList());

            // ✅ Tính tổng tồn kho trong controller
            int totalStock = productVariants.stream()
                    .mapToInt(v -> v.getQuantity() != null ? v.getQuantity() : 0)
                    .sum();

            model.addAttribute("product", product);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("brandName", brandName);
            model.addAttribute("ctProducts", productVariants);
            model.addAttribute("totalStock", totalStock); // ✅ Thêm totalStock

            return "CTProductList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

}
