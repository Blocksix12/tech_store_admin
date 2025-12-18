package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> getOrderItemsByOrderId(String orderId);
    OrderItem getOrderItemById(String orderItemId);
    List<OrderItem> getOrderItemsByStatus(String orderId, OrderItem.OrderItemStatus status);
}