package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Orders {
    @Id
    @UuidGenerator
    @Column(name = "order_id", columnDefinition = "CHAR(36)")
    private String orderID;

    @Column(name = "order_no", unique = true, length = 20)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private User user;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_id", nullable = false, columnDefinition = "CHAR(36)")
    private String shipping;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "nhanvienID", columnDefinition = "CHAR(36)")
    private String nhanvien;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public enum PaymentMethod {
        MOMO,
        VNPAY,
        STRIPE,
        COD;


        public static PaymentMethod toEnum(String value) {
            for(PaymentMethod method : PaymentMethod.values()){
                if (method.toString().equalsIgnoreCase(value)) return method;
            }
            return null;
        }
    }

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED;
    }
}