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
@IdClass(CTProductId.class)
@Table(name = "ctproducts")
public class CTProducts {
    @Id
    @Column(name = "product_id", nullable = false, columnDefinition = "CHAR(36)")
    private String productId;

    @Id
    @Column(name = "colorID", nullable = false, columnDefinition = "CHAR(36)")
    private String colorId;

    @Id
    @Column(name = "storageID", nullable = false, columnDefinition = "CHAR(36)")
    private String storageId;

    @Id
    @Column(name = "sizeID", nullable = false, columnDefinition = "CHAR(36)")
    private String sizeId;

    @Column(name = "price", nullable = false, columnDefinition = "CHAR(36)")
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
    @JoinColumn(name = "storageID", insertable = false, updatable = false)
    private Storage storage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sizeID", insertable = false, updatable = false)
    private DisplaySize size;
}
