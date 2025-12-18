package com.teamforone.tech_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "settings_shipping")
public class ShippingMethod {
    @Id
    @UuidGenerator
    private String id;

    private String methodName;
    private String description;
    private String logoUrl;
    private Boolean active = true;
}
