package com.teamforone.tech_store.controller. admin. crud;

import com.teamforone.tech_store.dto.request.BrandListDTO;
import com.teamforone.tech_store.dto.request.BrandRequest;
import com.teamforone.tech_store.enums.BrandStatus;
import com.teamforone.tech_store.model. Brands;
import com.teamforone.tech_store.service. admin.BrandService;
import com.teamforone.tech_store.service.admin.impl.FileStorageService;
import jakarta.validation.Valid;
import org.springframework. beans.factory.annotation.Autowired;
import org.springframework. stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.thymeleaf.util.NumberUtils.formatCurrency;

@Controller
@RequestMapping("/admin")
public class BrandController {

    private final BrandService brandService;
    private final FileStorageService fileStorageService;

    @Autowired
    public BrandController(BrandService brandService,
                           FileStorageService fileStorageService) {
        this.brandService = brandService;
        this.fileStorageService = fileStorageService;
    }

//    @GetMapping("/brands")
//    public String showBrandsPage(@RequestParam(required = false) String status, Model model) {
//        try {
//            List<Brands> brands;
//
//            // Filter by status if provided
//            if (status != null && !status.isEmpty()) {
//                try {
//                    BrandStatus brandStatus = BrandStatus.valueOf(status.toUpperCase());
//                    brands = brandService.findByStatus(brandStatus);
//                } catch (IllegalArgumentException e) {
//                    brands = brandService.getAllBrands();
//                }
//            } else {
//                brands = brandService.getAllBrands();
//            }
//
//            long totalBrands = brandService.countTotalBrands();
//            long activeBrands = brandService.countActiveBrands();
//            long inactiveBrands = brandService.countInactiveBrands();
//
//            // IMPORTANT: Always add brandRequest to model
//            model.addAttribute("brands", brands);
//            model.addAttribute("totalBrands", totalBrands);
//            model.addAttribute("activeBrands", activeBrands);
//            model.addAttribute("inactiveBrands", inactiveBrands);
//
//            // FIX: Always provide brandRequest even if empty
//            if (! model.containsAttribute("brandRequest")) {
//                model.addAttribute("brandRequest", new BrandRequest());
//            }
//
//            model.addAttribute("brandStatuses", BrandStatus.values());
//
//            // Data for topbar
//            model.addAttribute("pageTitle", "Quản lý Thương hiệu");
//            model.addAttribute("searchPlaceholder", "Tìm kiếm thương hiệu.. .");
//            model.addAttribute("searchId", "searchBrands");
//
//            // Breadcrumbs
//            List<Map<String, String>> breadcrumbs = new ArrayList<>();
//            breadcrumbs.add(Map. of("name", "Trang chủ", "url", "/admin"));
//            breadcrumbs.add(Map.of("name", "Sản phẩm", "url", "/admin/products"));
//            breadcrumbs. add(Map.of("name", "Thương hiệu", "url", ""));
//            model.addAttribute("breadcrumbs", breadcrumbs);
//
//            return "Brands";
//
//        } catch (Exception e) {
//            // FIX: Even on error, provide brandRequest
//            if (!model.containsAttribute("brandRequest")) {
//                model.addAttribute("brandRequest", new BrandRequest());
//            }
//            if (!model.containsAttribute("brands")) {
//                model.addAttribute("brands", new ArrayList<>());
//            }
//            if (!model.containsAttribute("brandStatuses")) {
//                model. addAttribute("brandStatuses", BrandStatus.values());
//            }
//
//            model.addAttribute("totalBrands", 0L);
//            model.addAttribute("activeBrands", 0L);
//            model.addAttribute("inactiveBrands", 0L);
//            model.addAttribute("errorMessage", "Lỗi khi tải danh sách thương hiệu: " + e.getMessage());
//
//            return "Brands";
//        }
//    }

    @GetMapping("/brands")
    public String showBrandsPage(@RequestParam(required = false) String status,
                                 @RequestParam(defaultValue = "false") boolean withStats,
                                 Model model) {
        try {
            // ✅ Get brands with stats
            List<BrandListDTO> brandsWithStats = brandService.getAllBrandsWithStats();

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                try {
                    BrandStatus brandStatus = BrandStatus.valueOf(status.toUpperCase());
                    brandsWithStats = brandsWithStats.stream()
                            .filter(b -> b.getStatus() == brandStatus)
                            .collect(java.util.stream.Collectors. toList());
                } catch (IllegalArgumentException e) {
                    // Keep all brands
                }
            }

            // Calculate totals
            long totalBrands = brandsWithStats.size();
            long activeBrands = brandsWithStats.stream()
                    .filter(b -> b. getStatus() == BrandStatus. ACTIVE)
                    .count();
            long inactiveBrands = totalBrands - activeBrands;

            // ✅ Calculate total revenue
            double totalRevenue = brandsWithStats.stream()
                    .mapToDouble(b -> b.getTotalRevenue() != null ? b.getTotalRevenue() : 0.0)
                    .sum();

            // ✅ Calculate total products
            long totalProducts = brandsWithStats.stream()
                    . mapToLong(b -> b.getProductCount() != null ? b.getProductCount() : 0L)
                    .sum();

            // ✅ Add stats to model
            model.addAttribute("brandsWithStats", brandsWithStats);
            model.addAttribute("totalBrands", totalBrands);
            model. addAttribute("activeBrands", activeBrands);
            model.addAttribute("inactiveBrands", inactiveBrands);
            model.addAttribute("totalRevenue", formatCurrency(totalRevenue));
            model.addAttribute("totalProducts", totalProducts);

            // Always provide brandRequest
            if (!model.containsAttribute("brandRequest")) {
                model.addAttribute("brandRequest", new BrandRequest());
            }

            model.addAttribute("brandStatuses", BrandStatus.values());

            // Data for topbar
            model.addAttribute("pageTitle", "Quản lý Thương hiệu");
            model.addAttribute("searchPlaceholder", "Tìm kiếm thương hiệu.. .");
            model.addAttribute("searchId", "searchBrands");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Sản phẩm", "url", "/admin/products"));
            breadcrumbs.add(Map.of("name", "Thương hiệu", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "Brands";

        } catch (Exception e) {
            // Error handling
            if (!model.containsAttribute("brandRequest")) {
                model.addAttribute("brandRequest", new BrandRequest());
            }
            if (!model.containsAttribute("brandsWithStats")) {
                model.addAttribute("brandsWithStats", new ArrayList<>());
            }
            if (!model.containsAttribute("brandStatuses")) {
                model.addAttribute("brandStatuses", BrandStatus.values());
            }

            model.addAttribute("totalBrands", 0L);
            model.addAttribute("activeBrands", 0L);
            model.addAttribute("inactiveBrands", 0L);
            model.addAttribute("totalRevenue", "0 ₫");
            model.addAttribute("totalProducts", 0L);
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách thương hiệu: " + e.getMessage());

            e.printStackTrace();
            return "Brands";
        }
    }

    private String formatCurrency(Double amount) {
        if (amount == null || amount == 0) {
            return "0 ₫";
        }

        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount) + " ₫";
    }

    @GetMapping("/brands/add")
    public String showAddBrandForm(Model model) {
        model.addAttribute("brandRequest", new BrandRequest());
        model. addAttribute("brandStatuses", BrandStatus.values());

        // Data for topbar
        model.addAttribute("pageTitle", "Thêm Thương hiệu");

        // Breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs. add(Map.of("name", "Trang chủ", "url", "/admin"));
        breadcrumbs.add(Map.of("name", "Thương hiệu", "url", "/admin/brands"));
        breadcrumbs.add(Map.of("name", "Thêm mới", "url", ""));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "AddBrand";
    }

    @PostMapping("/brands/add")
    public String addBrand(@Valid @ModelAttribute("brandRequest") BrandRequest brandRequest,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Validation errors
        if (result.hasErrors()) {
            model.addAttribute("brandStatuses", BrandStatus.values());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
            return "AddBrand";
        }

        try {
            if (brandRequest.getBrandName() == null || brandRequest.getBrandName().trim().isEmpty()) {
                model.addAttribute("brandStatuses", BrandStatus.values());
                model.addAttribute("errorMessage", "Tên thương hiệu không được để trống");
                return "AddBrand";
            }

            Brands savedBrand = brandService.addBrand(brandRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã thêm thương hiệu '" + savedBrand.getBrandName() + "' thành công!");

            return "redirect:/admin/brands";

        } catch (IllegalArgumentException e) {
            model.addAttribute("brandStatuses", BrandStatus.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "AddBrand";

        } catch (Exception e) {
            model.addAttribute("brandStatuses", BrandStatus. values());
            model.addAttribute("errorMessage", "Lỗi khi thêm thương hiệu: " + e.getMessage());
            return "AddBrand";
        }
    }

    @GetMapping("/brands/edit/{id}")
    public String showEditForm(@PathVariable("id") String brandID,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Brands brand = brandService.findBrandById(brandID);

            BrandRequest brandRequest = BrandRequest.builder()
                    . brandName(brand.getBrandName())
                    .status(brand.getStatus())
                    .description(brand.getDescription())
                    .logoUrl(brand.getLogoUrl())
                    .websiteUrl(brand.getWebsiteUrl())
                    .country(brand.getCountry())
                    .displayOrder(brand.getDisplayOrder())
                    .build();

            model.addAttribute("brand", brand);
            model.addAttribute("brandRequest", brandRequest);
            model.addAttribute("brandStatuses", BrandStatus.values());

            // Data for topbar
            model.addAttribute("pageTitle", "Chỉnh sửa Thương hiệu");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Thương hiệu", "url", "/admin/brands"));
            breadcrumbs.add(Map. of("name", "Chỉnh sửa", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "EditBrand"; // ✅ Đổi từ "BrandForm" thành "EditBrand"

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/brands";
        }
    }

    @PostMapping("/brands/update/{id}")
    public String updateBrand(@PathVariable("id") String brandID,
                              @Valid @ModelAttribute("brandRequest") BrandRequest brandRequest,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Brands brand = brandService.findBrandById(brandID);
            model.addAttribute("brand", brand);
            model.addAttribute("brandStatuses", BrandStatus.values());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
            return "EditBrand";
        }

        try {
            Brands updatedBrand = brandService.updateBrand(brandID, brandRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã cập nhật thương hiệu '" + updatedBrand.getBrandName() + "' thành công!");
            return "redirect:/admin/brands";

        } catch (IllegalArgumentException e) {
            Brands brand = brandService.findBrandById(brandID);
            model.addAttribute("brand", brand);
            model.addAttribute("brandStatuses", BrandStatus. values());
            model.addAttribute("errorMessage", e.getMessage());
            return "EditBrand";

        } catch (Exception e) {
            redirectAttributes. addFlashAttribute("errorMessage",
                    "Lỗi khi cập nhật thương hiệu: " + e.getMessage());
            return "redirect:/admin/brands/edit/" + brandID;
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String showDeleteConfirm(@PathVariable("id") String brandID,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            Brands brand = brandService.findBrandById(brandID);

            model.addAttribute("brand", brand);
            model.addAttribute("pageTitle", "Xóa Thương hiệu");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map. of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Thương hiệu", "url", "/admin/brands"));
            breadcrumbs. add(Map.of("name", "Xóa", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "DeleteBrand";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/brands";
        }
    }

    @PostMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") String brandID,
                              RedirectAttributes redirectAttributes) {
        try {
            Brands brand = brandService.findBrandById(brandID);
            String brandName = brand.getBrandName();
            String logoUrl = brand.getLogoUrl();

            // Xóa logo nếu có
            if (logoUrl != null && !logoUrl.isEmpty()) {
                try {
                    fileStorageService.deleteFile(logoUrl);
                } catch (Exception e) {
                    System.err.println("Không thể xóa logo: " + e.getMessage());
                }
            }

            // Xóa vĩnh viễn brand
            brandService.deleteBrand(brandID);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xóa thương hiệu '" + brandName + "' thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi xóa thương hiệu: " + e.getMessage());
        }

        return "redirect:/admin/brands";
    }

    @PostMapping("/brands/activate/{id}")
    public String activateBrand(@PathVariable("id") String brandID,
                                RedirectAttributes redirectAttributes) {
        try {
            brandService.activateBrand(brandID);
            Brands brand = brandService.findBrandById(brandID);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã kích hoạt thương hiệu '" + brand.getBrandName() + "' thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi kích hoạt thương hiệu: " + e.getMessage());
        }

        return "redirect:/admin/brands";
    }

    @ModelAttribute("brandRequest")
    public BrandRequest getBrandRequest() {
        return new BrandRequest();
    }
}