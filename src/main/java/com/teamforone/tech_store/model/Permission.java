package com.teamforone.tech_store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Builder
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "permission")
@NoArgsConstructor
public class Permission {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;
}

