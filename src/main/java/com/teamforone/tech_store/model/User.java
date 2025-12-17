package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE','LOCKED') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    // ✅ THÊM VÀO class User (sau dòng private Date updatedAt;)

    @Column(name = "customer_type", columnDefinition = "ENUM('NEW','REGULAR','VIP') DEFAULT 'NEW'")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType = CustomerType.NEW;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_spent")
    private Double totalSpent = 0.0;

    // ✅ THÊM enum CustomerType bên trong class User
    public enum CustomerType {
        NEW,
        REGULAR,
        VIP
    }

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum Status {
        ACTIVE,
        LOCKED;

        private static Status toEnum(String status) {
            for (Status item : values()) {
                if (item.toString().equalsIgnoreCase(status)) return item;
            }
            return null;
        }
    }

}
