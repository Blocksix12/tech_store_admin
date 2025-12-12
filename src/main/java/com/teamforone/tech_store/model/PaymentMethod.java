package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "settings_payment_methods")
public class PaymentMethod {
    @Id
    @UuidGenerator
    private String id;

    private String methodName;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String logoUrl;
    private Boolean active = true;
}
