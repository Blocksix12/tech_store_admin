package com.teamforone.tech_store.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchInventoryRequest {
    private List<InventoryRequest> items;
}