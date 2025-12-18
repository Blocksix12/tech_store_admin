package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    List<Orders> findByStatus(Orders.OrderStatus status);
}

