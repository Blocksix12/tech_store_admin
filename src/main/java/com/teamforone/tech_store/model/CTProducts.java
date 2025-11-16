package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ctproducts")
@IdClass(CTProductId.class)
public class CTProducts {
    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;

    @Id
    @Column(name = "colorID", nullable = false)
    private String colorId;


    @Id
    @Column(name = "sizeID", nullable = false)
    private String sizeId;

    @Id
    @Column(name = "storageID", nullable = false)
    private String storageId;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // ----- Relationships -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorID", insertable = false, updatable = false)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sizeID", insertable = false, updatable = false)
    private DisplaySize size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storageID", insertable = false, updatable = false)
    private Storage storage;
}
