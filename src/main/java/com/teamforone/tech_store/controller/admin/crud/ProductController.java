package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.dto.request.CTProductRequest;
import com.teamforone.tech_store.dto.request.ProductListDTO;
import com.teamforone.tech_store.dto.request.ProductRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Brands;
import com.teamforone.tech_store.model.Categories;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.service.admin.BrandService;
import com.teamforone.tech_store.service.admin.CTProductService;
import com.teamforone.tech_store.service.admin.CategoryService;
import com.teamforone.tech_store.service.admin.ProductService;
import com.teamforone.tech_store.service.admin.impl.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
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
        model.addAttribute("statusOptions", productService.getAllProductStatuses());
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());

        model.addAttribute("colors", ctProductService.getAllColors());
        model.addAttribute("storages", ctProductService.getAllStorages());
        model.addAttribute("sizes", ctProductService.getAllSizes());
        return "AddProducts";
    }

    @PostMapping("/products/add")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
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
            model.addAttribute("categories", categoryService. getAllCategories());
            model.addAttribute("brands", brandService. getAllBrands());
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
            model.addAttribute("categories", categoryService. getAllCategories());
            model.addAttribute("brands", brandService. getAllBrands());
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
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/products/import")
    public String importProducts(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file để tải lên.");
            return "redirect:/admin/products";
        }

        try (Workbook workbook = WorkbookFactory.create(file. getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Product> productsToImport = new ArrayList<>();

            int successCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();

            // ✅ Start from row 1 (skip header at row 0)
            for (int i = 1; i <= sheet. getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Product product = new Product();

                    // ✅ Column 0: ID (Skip for new products)
                    String productId = getCellValueAsString(row.getCell(0));
                    if (!productId.isEmpty() && ! productId.equals("null")) {
                        // Check if product exists for update
                        Product existingProduct = productService.findProductById(productId);
                        if (existingProduct != null) {
                            product = existingProduct; // Update existing
                        }
                    }

                    // ✅ Column 1: Name (REQUIRED)
                    String name = getCellValueAsString(row.getCell(1));
                    if (name.isEmpty()) {
                        errors.append("Dòng "). append(i + 1).append(": Tên sản phẩm không được để trống.  ");
                        errorCount++;
                        continue;
                    }
                    product.setName(name);

                    // ✅ Column 2: Slug
                    String slug = getCellValueAsString(row.getCell(2));
                    if (slug.isEmpty()) {
                        slug = name.toLowerCase()
                                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                                .replaceAll("[ìíịỉĩ]", "i")
                                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                                .replaceAll("[ùúụủũưừứựửữ]", "u")
                                .replaceAll("[ỳýỵỷỹ]", "y")
                                .replaceAll("[đ]", "d")
                                .replaceAll("\\s+", "-")
                                .replaceAll("[^a-z0-9-]", "")
                                .replaceAll("-+", "-")
                                .replaceAll("^-|-$", "");
                    }
                    product.setSlug(slug);

                    // ✅ Column 3: Description
                    product.setDescription(getCellValueAsString(row.getCell(3)));

                    String categoryName = getCellValueAsString(row.getCell(4));
                    if (! categoryName.isEmpty()) {
                        Optional<Categories> category = categoryService.findCategoryByName(categoryName);
                        if (category.isPresent()) {
                            product.setCategoryId(category.get().getCategoryId());
                        } else {
                            errors.append("Dòng ").append(i + 1)
                                    .append(": Danh mục '").append(categoryName)
                                    .append("' không tồn tại. ");
                            errorCount++;
                            continue;
                        }
                    } else {
                        errors.append("Dòng ").append(i + 1)
                                .append(": Danh mục không được để trống. ");
                        errorCount++;
                        continue;
                    }

                    // ✅ Column 5: Brand Name → Lookup ID
                    String brandName = getCellValueAsString(row.getCell(5));
                    if (!brandName.isEmpty()) {
                        Optional<Brands> brand = brandService.findByBrandName(brandName);
                        if (brand.isPresent()) {
                            product.setBrandId(brand.get().getBrandID());
                        } else {
                            errors.append("Dòng ").append(i + 1)
                                    . append(": Thương hiệu '").append(brandName)
                                    .append("' không tồn tại. ");
                            errorCount++;
                            continue;
                        }
                    } else {
                        errors. append("Dòng "). append(i + 1)
                                .append(": Thương hiệu không được để trống. ");
                        errorCount++;
                        continue;
                    }

                    // ✅ Column 6-8: Skip (Min Price, Max Price, Stock Count - calculated)
                    String priceStr = getCellValueAsString(row.getCell(6));
                    Double price = null;
                    if (!priceStr.isEmpty()) {
                        try {
                            price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
                            if (price <= 0) {
                                errors.append("Dòng ").append(i + 1)
                                        . append(": Giá phải lớn hơn 0. ");
                                errorCount++;
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            errors.append("Dòng ").append(i + 1)
                                    .append(": Giá không hợp lệ. ");
                            errorCount++;
                            continue;
                        }
                    }

                    // Column 7: Sale Price
                    String salePriceStr = getCellValueAsString(row.getCell(7));
                    Double salePrice = null;
                    if (!salePriceStr.isEmpty()) {
                        try {
                            salePrice = Double.parseDouble(salePriceStr.replaceAll("[^0-9.]", ""));
                            if (salePrice > price) {
                                errors. append("Dòng "). append(i + 1)
                                        .append(": Giá khuyến mãi phải nhỏ hơn giá gốc. ");
                                errorCount++;
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }

                    // Column 8: Quantity
                    String quantityStr = getCellValueAsString(row.getCell(8));
                    Integer quantity = 0;
                    if (!quantityStr.isEmpty()) {
                        try {
                            quantity = Integer.parseInt(quantityStr.replaceAll("[^0-9]", ""));
                            if (quantity < 0) {
                                errors.append("Dòng ").append(i + 1)
                                        . append(": Số lượng không được âm. ");
                                errorCount++;
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            errors.append("Dòng ").append(i + 1)
                                    .append(": Số lượng không hợp lệ. ");
                            errorCount++;
                            continue;
                        }
                    }



                    // ✅ Column 9: Stock Status (Skip - calculated)


                    // ✅ Column 10: Product Status
                    Cell statusCell = row.getCell(10); // K = column 10
                    String cellValue = getCellValueAsString(statusCell);
                    Product.Status status = Product.Status.fromString(cellValue);
                    if (status == null){
                        status = Product.Status.DRAFT;
                    }
                    product.setProductStatus(status);

                    // ✅ Column 11: Image URL
                    product.setImageUrl(getCellValueAsString(row.getCell(11)));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    // ✅ Column 12: Created At
                    Cell dateCell = row.getCell(12);
                    if (dateCell != null) {
                        if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                            product.setCreatedAt(dateCell.getDateCellValue());
                        } else {
                            String dateStr = getCellValueAsString(dateCell);
                            if (!dateStr.isEmpty()) {
                                product.setCreatedAt(sdf.parse(dateStr));
                            }
                        }
                    }

                    // Column 12: Updated At
                    Cell dateCell2 = row.getCell(13);
                    if (dateCell2 != null) {
                        if (dateCell2.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell2)) {
                            product.setUpdatedAt(dateCell2.getDateCellValue());
                        } else {
                            String dateStr = getCellValueAsString(dateCell2);
                            if (! dateStr.isEmpty()) {
                                product.setUpdatedAt(sdf.parse(dateStr));
                            }
                        }
                    }

                    productsToImport.add(product);
                    successCount++;

                } catch (Exception e) {
                    errors.append("Dòng ").append(i + 1).append(": "). append(e.getMessage()).append(". ");
                    errorCount++;
                    e.printStackTrace(); // ✅ Log for debugging
                }
            }

            // ✅ Save valid products
            if (! productsToImport.isEmpty()) {
                productService.saveAll(productsToImport);
            }

            // ✅ Prepare message
            String message = "Import thành công " + successCount + " sản phẩm. ";
            if (errorCount > 0) {
                message += " Có " + errorCount + " lỗi: " + errors.toString();
                redirectAttributes.addFlashAttribute("warning", message);
            } else {
                redirectAttributes.addFlashAttribute("success", message);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Import thất bại: " + e. getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }

    // ===== EXPORT METHOD - Updated Version =====
    @GetMapping("/products/export")
    public void exportProducts(HttpServletResponse response) throws IOException {
        // ✅ Get products with full information (same as list view)
        List<ProductListDTO> products = productService.getAllProducts();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response. setHeader("Content-Disposition", "attachment; filename=products_export_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Products");

            // ===== STYLE SETUP =====
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook. createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors. WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors. DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType. SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle. THIN);
            headerStyle.setBorderRight(BorderStyle. THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle normalStyle = workbook.createCellStyle();
            normalStyle.setBorderBottom(BorderStyle.THIN);
            normalStyle.setBorderTop(BorderStyle.THIN);
            normalStyle.setBorderLeft(BorderStyle.THIN);
            normalStyle.setBorderRight(BorderStyle.THIN);
            normalStyle.setWrapText(true);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderTop(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);
            numberStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));

            CellStyle priceStyle = workbook.createCellStyle();
            priceStyle.setBorderBottom(BorderStyle. THIN);
            priceStyle.setBorderTop(BorderStyle. THIN);
            priceStyle.setBorderLeft(BorderStyle. THIN);
            priceStyle.setBorderRight(BorderStyle. THIN);
            priceStyle.setDataFormat(createHelper. createDataFormat().getFormat("#,##0 \"₫\""));

            // ===== HEADER ROW =====
            Row headerRow = sheet. createRow(0);
            String[] columns = {
                    "ID", "Tên sản phẩm", "SKU", "Mô tả", "Danh mục",
                    "Thương hiệu", "Giá thấp nhất", "Giá cao nhất",
                    "Tồn kho", "Trạng thái tồn kho", "Trạng thái sản phẩm",
                    "Hình ảnh", "Ngày tạo", "Ngày cập nhật"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Set header row height
            headerRow.setHeightInPoints(25);

            // ===== DATA ROWS =====
            int rowIdx = 1;
            for (ProductListDTO product : products) {
                Row row = sheet. createRow(rowIdx++);

                // Column 0: ID
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(product.getId() != null ? product.getId() : "");
                cell0.setCellStyle(normalStyle);

                // Column 1: Name
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(product.getName() != null ? product. getName() : "");
                cell1.setCellStyle(normalStyle);

                // Column 2: SKU (Slug)
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(product.getSku() != null ? product.getSku() : "");
                cell2.setCellStyle(normalStyle);

                // Column 3: Description
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(product.getDescription() != null ?  product.getDescription() : "");
                cell3.setCellStyle(normalStyle);

                // Column 4: Category Name
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(product.getCategory() != null ? product.getCategory() : "");
                cell4.setCellStyle(normalStyle);

                // Column 5: Brand Name
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(product.getBrands() != null ? product.getBrands() : "");
                cell5.setCellStyle(normalStyle);

                // Column 6: Min Price
                Cell cell6 = row.createCell(6);
                if (product.getMinPrice() != null && product.getMinPrice() > 0) {
                    cell6.setCellValue(product.getMinPrice());
                    cell6.setCellStyle(priceStyle);
                } else {
                    cell6.setCellValue("Liên hệ");
                    cell6.setCellStyle(normalStyle);
                }

                // Column 7: Max Price
                Cell cell7 = row.createCell(7);
                if (product.getMaxPrice() != null && product.getMaxPrice() > 0) {
                    cell7.setCellValue(product.getMaxPrice());
                    cell7. setCellStyle(priceStyle);
                } else {
                    cell7.setCellValue("Liên hệ");
                    cell7.setCellStyle(normalStyle);
                }

                // Column 8: Stock Count
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(product. getStockCount() != null ?  product.getStockCount() : 0);
                cell8.setCellStyle(numberStyle);

                // Column 9: Stock Status Text
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(product.getStockStatusText() != null ? product.getStockStatusText() : "");

                // Color coding for stock status
                CellStyle stockStyle = workbook. createCellStyle();
                stockStyle.cloneStyleFrom(normalStyle);
                if ("Hết hàng".equals(product.getStockStatusText())) {
                    stockStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                    stockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } else if ("Sắp hết". equals(product.getStockStatusText())) {
                    stockStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    stockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } else if ("Còn hàng".equals(product.getStockStatusText())) {
                    stockStyle.setFillForegroundColor(IndexedColors. LIGHT_GREEN.getIndex());
                    stockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                cell9.setCellStyle(stockStyle);

                // Column 10: Product Status Text
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(product.getStatusText() != null ? product.getStatusText() : "");

                // Color coding for product status
                CellStyle statusStyle = workbook.createCellStyle();
                statusStyle.cloneStyleFrom(normalStyle);
                if ("Đang bán".equals(product.getStatusText())) {
                    statusStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                    statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } else if ("Ngừng bán".equals(product.getStatusText())) {
                    statusStyle.setFillForegroundColor(IndexedColors. GREY_25_PERCENT.getIndex());
                    statusStyle.setFillPattern(FillPatternType. SOLID_FOREGROUND);
                } else if ("Nháp".equals(product.getStatusText())) {
                    statusStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                    statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                cell10.setCellStyle(statusStyle);

                // Column 11: Image URL
                Cell cell11 = row.createCell(11);
                cell11.setCellValue(product.getImageUrl() != null ? product.getImageUrl() : "");
                cell11.setCellStyle(normalStyle);

                CellStyle dateCellStyle = workbook.createCellStyle();
                short dateFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd");
                dateCellStyle.setDataFormat(dateFormat);

                // Column 12: Created At (if available)
                Cell cell12 = row.createCell(12);
                cell12.setCellValue(product.getCreatedAt()); // Add if you have this field
                cell12.setCellStyle(dateCellStyle);

                // Column 13: Updated At (if available)
                Cell cell13 = row.createCell(13);
                cell13.setCellValue(product.getUpdatedAt()); // Add if you have this field
                cell13.setCellStyle(dateCellStyle);
            }

            // ===== AUTO-SIZE COLUMNS =====
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);

                // Set minimum and maximum width
                if (i == 3) { // Description column
                    sheet.setColumnWidth(i, 15000); // Wider for description
                } else if (i == 11) { // Image URL column
                    sheet.setColumnWidth(i, 10000);
                } else {
                    sheet.setColumnWidth(i, Math.min(currentWidth + 1000, 8000));
                }
            }

            // ===== FREEZE HEADER ROW =====
            sheet. createFreezePane(0, 1);

            // ===== WRITE TO RESPONSE =====
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private Product.Status getStatusFromCell(Cell cell) {
        String cellValue = getCellValueAsString(cell);
        if (cellValue.isEmpty()) return null;

        // Thử chuyển đổi từ tên enum (DRAFT, PUBLISHED, ARCHIVED)
        Product.Status status = Product.Status.toEnum(cellValue);
        if (status != null) return status;

        // Thử chuyển đổi từ displayName (Nháp, Xuất bản, Lưu trữ)
        return Product.Status.fromDisplayName(cellValue);
    }


    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

}
