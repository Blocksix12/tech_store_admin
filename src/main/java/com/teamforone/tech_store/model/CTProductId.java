package com.teamforone.tech_store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CTProductId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String colorId;
    private String sizeId;
    private String storageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTProductId that = (CTProductId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(colorId, that.colorId) &&
                Objects.equals(sizeId, that.sizeId) &&
                Objects.equals(storageId, that.storageId);
    }


    @Override
    public int hashCode(){
        return Objects.hash(productId, colorId, sizeId, storageId);
    }
}
