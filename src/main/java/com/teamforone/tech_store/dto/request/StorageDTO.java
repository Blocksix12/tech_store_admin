package com.teamforone.tech_store.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageDTO {
    private String id;
    private String name; // Format: "RAM/ROM" (e.g., "8GB/128GB")
    private String ram;
    private String rom;
}
