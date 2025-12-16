package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.OrderItem;
import com.teamforone.tech_store.repository.admin.crud.OrderItemRepository;
import com.teamforone.tech_store.service.admin.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> getOrderItemsByOrderId(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public OrderItem getOrderItemById(String orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order Item not found"));
    }

    @Override
    public List<OrderItem> getOrderItemsByStatus(String orderId, OrderItem.OrderItemStatus status) {
        return orderItemRepository.findByOrderIdAndStatus(orderId, status);
    }
}