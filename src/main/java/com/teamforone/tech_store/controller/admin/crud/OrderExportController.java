package com.teamforone.tech_store.controller.admin.crud;

import com.teamforone.tech_store.model.Orders;
import com.teamforone.tech_store.service.admin.OrderService;
import com.teamforone.tech_store.service.admin.OrderPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderExportController {

    private final OrderService orderService;
    private final OrderPdfService pdfService;

    // Xuất PDF một đơn hàng
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportOrderPdf(@PathVariable String id) {
        try {
            Orders order = orderService.getOrderById(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfContent = pdfService.generateOrderDetailPdf(order);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "order_" + order.getOrderNo() + ".pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Xuất PDF tất cả đơn hàng
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportAllOrdersPdf(
            @RequestParam(required = false) String status) {
        try {
            List<Orders> orders;

            if (status != null && !status.isEmpty()) {
                Orders.OrderStatus orderStatus = Orders.OrderStatus.valueOf(status);
                orders = orderService.getOrdersByStatus(orderStatus);
            } else {
                orders = orderService.getAllOrders();
            }

            byte[] pdfContent = pdfService.generateOrderListPdf(orders);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "orders_" + System.currentTimeMillis() + ".pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}