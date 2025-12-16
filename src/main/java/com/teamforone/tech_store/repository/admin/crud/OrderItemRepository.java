package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {


    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderID = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") String orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderID = :orderId AND oi.status = :status")
    List<OrderItem> findByOrderIdAndStatus(@Param("orderId") String orderId,
                                           @Param("status") OrderItem.OrderItemStatus status);
}