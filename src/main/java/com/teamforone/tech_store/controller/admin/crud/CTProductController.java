package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.CTProductRequest;
import com.teamforone.tech_store.service.admin.CTProductService;
import com.teamforone.tech_store.service.admin.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CTProductController {
    private final CTProductService ctProductService;
    private final ProductService productService;

    public CTProductController(CTProductService ctProductService, ProductService productService) {
        this.ctProductService = ctProductService;
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    @GetMapping("/product-variants")
    public String listCTProducts(
            @RequestParam(required = false) String productFilter,
            @RequestParam(required = false) String colorFilter,
            @RequestParam(required = false) String storageFilter,
            @RequestParam(required = false) String stockFilter,
            @RequestParam(required = false) String search,
            Model model
    ) {
        // Lấy tất cả sản phẩm
        List<CTProductRequest> allProducts = ctProductService.getAllProduct();

        // Apply filters
        List<CTProductRequest> filteredProducts = allProducts.stream()
                .filter(p -> productFilter == null || productFilter.isEmpty()
                        || (p.getProductName() != null && p.getProductName().equals(productFilter)))
                .filter(p -> colorFilter == null || colorFilter.isEmpty()
                        || (p.getColorName() != null && p.getColorName().equals(colorFilter)))
                .filter(p -> storageFilter == null || storageFilter.isEmpty()
                        || (p.getRomName() != null && p.getRomName().equals(storageFilter)))
                .filter(p -> {
                    if (stockFilter == null || stockFilter.isEmpty()) return true;
                    switch (stockFilter) {
                        case "in": return p.getQuantity() != null && p.getQuantity() > 10;
                        case "low": return p.getQuantity() != null && p.getQuantity() > 0 && p.getQuantity() <= 10;
                        case "out": return p.getQuantity() != null && p.getQuantity() == 0;
                        default: return true;
                    }
                })
                .filter(p -> search == null || search.isEmpty()
                        || (p. getProductName() != null && p.getProductName().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());

        // Tạo danh sách unique values cho filters
        List<String> uniqueProducts = allProducts.stream()
                .map(CTProductRequest::getProductName)
                .filter(name -> name != null && ! name.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> uniqueColors = allProducts.stream()
                .map(CTProductRequest::getColorName)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> uniqueStorages = allProducts.stream()
                .map(CTProductRequest::getRomName)
                .filter(name -> name != null && ! name.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Add to model
        model.addAttribute("ctProducts", filteredProducts);
        model.addAttribute("totalElements", filteredProducts.size());

        // Add unique values for filters
        model.addAttribute("uniqueProducts", uniqueProducts);
        model.addAttribute("uniqueColors", uniqueColors);
        model.addAttribute("uniqueStorages", uniqueStorages);

        // For modal dropdowns
        model.addAttribute("products", ctProductService.getAllProduct());
        model.addAttribute("colors", ctProductService.getAllColors());
        model.addAttribute("storages", ctProductService.getAllStorages());
        model.addAttribute("sizes", ctProductService.getAllSizes());

        return "CTProductAdmin";
    }

    @GetMapping("/product-variants/add")
    public String showAddVariantForm(Model model) {
        // QUAN TRỌNG: Phải add object vào model
        model.addAttribute("ctProductRequest", new CTProductRequest());

        // Populate dropdowns
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("colors", ctProductService.getAllColors());
        model.addAttribute("storages", ctProductService.getAllStorages());
        model.addAttribute("sizes", ctProductService.getAllSizes());

        return "AddCTProduct";
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/product-variants/add")
    public String addProductVariant(
            @Valid @ModelAttribute("ctProductRequest") CTProductRequest ctProductRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validation
        if (result.hasErrors()) {
            // Populate lại dropdowns để hiển thị form
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddCTProduct";
        }

        try {
            // Validate logic nghiệp vụ
            validateVariantRequest(ctProductRequest);

            // Lưu biến thể
            ctProductService.addProductVariant(ctProductRequest);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success",
                    "Biến thể sản phẩm đã được thêm thành công!");

            return "redirect:/admin/product-variants";

        } catch (IllegalArgumentException e) {
            // Lỗi validate nghiệp vụ
            model.addAttribute("error", e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddCTProduct";

        } catch (Exception e) {
            // Lỗi không xác định
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("colors", ctProductService.getAllColors());
            model.addAttribute("storages", ctProductService.getAllStorages());
            model.addAttribute("sizes", ctProductService.getAllSizes());
            return "AddCTProduct";
        }
    }
    /**
     * Validate nghiệp vụ
     */
    private void validateVariantRequest(CTProductRequest request) {
        // Validate giá gốc > 0
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0!");
        }

        // Validate giá khuyến mãi < giá gốc
        if (request.getSalePrice() != null &&
                request.getSalePrice() > 0 &&
                request.getSalePrice() >= request.getPrice()) {
            throw new IllegalArgumentException(
                    "Giá khuyến mãi phải nhỏ hơn giá gốc!");
        }

        // Validate số lượng >= 0
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng không được âm!");
        }

        // Validate required fields
        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm!");
        }

        if (request.getColorId() == null || request.getColorId().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn màu sắc!");
        }
    }
}
