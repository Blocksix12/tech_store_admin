package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart")
public class Cart {
    @Id
    @UuidGenerator
    @Column(name = "cart_id", columnDefinition = "CHAR(36)")
    private String cartID;

    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String user;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    // 1 giỏ hàng có nhiều item
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
}
