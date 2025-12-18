// ========================================
// FILE 1: InventoryWebController.java
// ========================================
package com.teamforone.tech_store.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class InventoryWebController {

    @GetMapping("/inventory")
    public String showInventoryPage(Model model) {
        model.addAttribute("pageTitle", "Quản lý Tồn kho");

        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
        breadcrumbs.add(Map.of("name", "Tồn kho", "url", ""));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "admin/quanlytonkho";
    }

    @GetMapping("/inventory/warehouses")
    public String showWarehouses(Model model) {
        model.addAttribute("pageTitle", "Quản lý Kho hàng");
        return "admin/warehouses";
    }

    @GetMapping("/inventory/stock-in")
    public String showStockIn(Model model) {
        model.addAttribute("pageTitle", "Nhập kho");
        return "admin/stock-in";
    }

    @GetMapping("/inventory/stock-out")
    public String showStockOut(Model model) {
        model.addAttribute("pageTitle", "Xuất kho");
        return "admin/stock-out";
    }

    @GetMapping("/inventory/transfer")
    public String showTransfer(Model model) {
        model.addAttribute("pageTitle", "Điều chuyển kho");
        return "admin/transfer";
    }

    @GetMapping("/inventory/alerts")
    public String showAlerts(Model model) {
        model.addAttribute("pageTitle", "Cảnh báo Tồn kho");
        return "admin/inventory-alerts";
    }
}