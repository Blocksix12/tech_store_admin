package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.model.Orders;
import com.teamforone.tech_store.model.OrderItem;
import com.teamforone.tech_store.service.admin.OrderService;
import com.teamforone.tech_store.service.admin.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/quanlidonhang")
    public String viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Model model
    ) {
        // Lấy tất cả đơn hàng
        List<Orders> orders = orderService.getAllOrders();

        // Đếm theo trạng thái
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == Orders.OrderStatus.PENDING).count();
        long processingOrders = orders.stream().filter(o -> o.getStatus() == Orders.OrderStatus.PROCESSING).count();
        long shippedOrders = orders.stream().filter(o -> o.getStatus() == Orders.OrderStatus.SHIPPED).count();
        long deliveredOrders = orders.stream().filter(o -> o.getStatus() == Orders.OrderStatus.DELIVERED).count();
        long cancelledOrders = orders.stream().filter(o -> o.getStatus() == Orders.OrderStatus.CANCELLED).count();

        // Truyền dữ liệu vào view
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("shippedOrders", shippedOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);

        return "admin/quanlidonhang";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable String id, Model model) {
        // Lấy thông tin đơn hàng
        Orders order = orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/admin/quanlidonhang?error=not-found";
        }

        // Lấy danh sách items trong đơn hàng
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(id);

        // Tính tổng số lượng sản phẩm
        int totalItems = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // Truyền dữ liệu vào view
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + order.getOrderNo());

        return "admin/order-detail";
    }
}