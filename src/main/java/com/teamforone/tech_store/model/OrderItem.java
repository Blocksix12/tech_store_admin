package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @UuidGenerator
    @Column(name = "order_item_id", columnDefinition = "CHAR(36)")
    private String orderItemID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorID", nullable = false)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sizeID", nullable = false)
    private DisplaySize displaySize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storageID", nullable = false)
    private Storage storage;

    @Column(name = "quantity", columnDefinition = "INT DEFAULT 1")
    private Integer quantity;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderItemStatus status = OrderItemStatus.PENDING;

    public enum OrderItemStatus {
        PENDING,
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED;

        public static OrderItemStatus toEnum(String status) {
            for (OrderItemStatus item : values()) {
                if (item.toString().equalsIgnoreCase(status)) return item;
            }
            return null;
        }
    }
}