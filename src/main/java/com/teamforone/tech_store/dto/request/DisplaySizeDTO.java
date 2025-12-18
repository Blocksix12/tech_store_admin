package com.teamforone.tech_store.dto.request;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisplaySizeDTO {
    private String id;
    private String name; // Format: "6.7\" FHD+"
    private BigDecimal sizeInch;
    private String resolution;
    private String technology;
    private String refreshRate;
}
