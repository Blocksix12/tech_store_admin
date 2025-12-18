package com.teamforone.tech_store.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReportDTO {
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String customerType;
    private Integer totalOrders;
    private Double totalSpent;
    private String formattedTotalSpent;
    private String registeredDate;
}