package com.teamforone.tech_store.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsGeneralDTO {
    private String websiteName;
    private String slogan;
    private String websiteUrl;
    private String contactEmail;
    private String description;
    private String companyName;
    private String phone;
    private String address;
    private String taxCode;
    private String hotline;
    private String language;
    private String timezone;
    private String currency;
    private String dateFormat;
    private String logoUrl;

    // 🔥 giống Product
    private MultipartFile logoFile;
}
