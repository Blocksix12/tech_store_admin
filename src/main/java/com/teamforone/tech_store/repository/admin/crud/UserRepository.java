package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    long countByStatus(User.Status status);
    // ✅ THÊM VÀO interface UserRepository

    // Đếm khách VIP
    long countByCustomerType(User.CustomerType customerType);

    // Đếm khách hàng mới trong 30 ngày
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countNewCustomers(@Param("startDate") Date startDate);

    // Top khách hàng theo tổng chi tiêu
    List<User> findTop10ByOrderByTotalSpentDesc();

    // Đếm khách có >= 2 đơn (để tính return rate)
    @Query("SELECT COUNT(u) FROM User u WHERE u.totalOrders >= 2")
    long countReturningCustomers();

    // Đếm khách có ít nhất 1 đơn
    @Query("SELECT COUNT(u) FROM User u WHERE u.totalOrders > 0")
    long countCustomersWithOrders();
}
