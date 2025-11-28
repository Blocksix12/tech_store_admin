package com.teamforone.tech_store.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Dashboard"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/danhsach")
    public String danhsach() {
        return "ProductList"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("danhmuc")
    public String danhmuc() {
        return "Categories"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/thuonghieu")
    public String thuonghieu() {
        return "Brands"; // KHÔNG cần /templates, chỉ tên file
    }

//    @GetMapping("/products/add")
//    public String addProduct() {
//        return "AddProducts"; // KHÔNG cần /templates, chỉ tên file
//    }

    @GetMapping("/login")
    public String login() {
        return "login"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/import")
    public String importPage() {
        return "Import-Export"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/profile")
    public String profile() {
        return "Profile"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/settings")
    public String settings() {
        return "Settings"; // KHÔNG cần /templates, chỉ tên file
    }

    @GetMapping("/CTProduct")
    public String ctProduct() {
        return "CTProductAdmin"; // KHÔNG cần /templates, chỉ tên file
    }
}
