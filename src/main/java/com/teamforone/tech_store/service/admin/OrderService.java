package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.Orders;

import java.util.List;

public interface OrderService {
    List<Orders> getAllOrders();
    Orders getOrderById(String id);
    Orders updateOrderStatus(String id, String status);
    void deleteOrder(String id);
}

