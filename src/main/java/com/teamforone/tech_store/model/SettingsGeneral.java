package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "settings_general")
public class SettingsGeneral {
    @Id
    @UuidGenerator
    private String id;

    private String websiteName;
    private String slogan;
    private String websiteUrl;
    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String logoUrl;

    private String companyName;
    private String phone;
    private String address;
    private String taxCode;
    private String hotline;

    private String language;
    private String timezone;
    private String currency;
    private String dateFormat;
}
