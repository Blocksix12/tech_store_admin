package com.teamforone.tech_store.dto.request;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryRequest {
    private String productId;
    private String colorId;
    private String storageId;
    private String sizeId;
    private Integer quantity;
}
