package com.teamforone.tech_store.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageDTO {
    private String id;
    private String name;
    private String ram;
    private String rom;
}
