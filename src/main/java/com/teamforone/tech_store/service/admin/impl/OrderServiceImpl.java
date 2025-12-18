package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.Orders;
import com.teamforone.tech_store.repository.admin.crud.OrderRepository;
import com.teamforone.tech_store.service.admin.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Orders getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Orders updateOrderStatus(String id, String status) {
        Orders order = getOrderById(id);

        try {
            Orders.OrderStatus statusEnum = Orders.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        return orderRepository.save(order);
    }

    @Override
    public List<Orders> getOrdersByStatus(Orders.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
}

