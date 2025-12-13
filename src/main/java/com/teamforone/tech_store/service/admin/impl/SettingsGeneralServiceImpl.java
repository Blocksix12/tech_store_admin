package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.SettingsGeneralDTO;
import com.teamforone.tech_store.model.SettingsGeneral;
import com.teamforone.tech_store.repository.admin.SettingsGeneralRepository;
import com.teamforone.tech_store.service.admin.SettingsGeneralService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SettingsGeneralServiceImpl implements SettingsGeneralService {
    private final SettingsGeneralRepository repo;
    private final FileStorageService fileStorageService;

    public SettingsGeneralServiceImpl(SettingsGeneralRepository repo,
                                      FileStorageService fileStorageService) {
        this.repo = repo;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public SettingsGeneralDTO get() {
        return mapToDTO(repo.findAll().stream().findFirst().orElse(null));
    }

    @Override
    @Transactional
    public SettingsGeneral saveSettings(SettingsGeneralDTO req) {

        // ========= 1. VALIDATE =========
        if (req.getWebsiteName() == null || req.getWebsiteName().isBlank()) {
            throw new IllegalArgumentException("Tên website không được để trống");
        }

        if (req.getContactEmail() != null &&
                !req.getContactEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }

        // ========= 2. LẤY SETTINGS HIỆN TẠI =========
        SettingsGeneral current = repo
                .findTopByOrderByIdAsc()
                .orElse(null);

        // ========= 3. XỬ LÝ LOGO =========
        String logoUrl = current != null ? current.getLogoUrl() : null;

        if (req.getLogoFile() != null && !req.getLogoFile().isEmpty()) {
            try {
                logoUrl = fileStorageService.saveFile(req.getLogoFile());
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload logo", e);
            }
        }

        // ========= 4. BUILD ENTITY =========
        SettingsGeneral settings = SettingsGeneral.builder()
                .id(current != null ? current.getId() : null) // QUAN TRỌNG
                .websiteName(req.getWebsiteName())
                .slogan(req.getSlogan())
                .websiteUrl(req.getWebsiteUrl())
                .contactEmail(req.getContactEmail())
                .description(req.getDescription())
                .companyName(req.getCompanyName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .taxCode(req.getTaxCode())
                .hotline(req.getHotline())
                .language(req.getLanguage())
                .timezone(req.getTimezone())
                .currency(req.getCurrency())
                .dateFormat(req.getDateFormat())
                .logoUrl(logoUrl) // ✅ LUÔN GIỮ LOGO
                .build();

        // ========= 5. SAVE =========
        return repo.save(settings);
    }



    private SettingsGeneralDTO mapToDTO(SettingsGeneral entity) {
        if (entity == null) return null;

        return SettingsGeneralDTO.builder()
                .websiteName(entity.getWebsiteName())
                .slogan(entity.getSlogan())
                .websiteUrl(entity.getWebsiteUrl())
                .contactEmail(entity.getContactEmail())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl()) // ✅ CHỈ URL
                .companyName(entity.getCompanyName())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .taxCode(entity.getTaxCode())
                .hotline(entity.getHotline())
                .language(entity.getLanguage())
                .timezone(entity.getTimezone())
                .currency(entity.getCurrency())
                .dateFormat(entity.getDateFormat())
                .build();
    }


    private SettingsGeneral mapToEntity(SettingsGeneralDTO dto) {
        if (dto == null) return null;

        return SettingsGeneral.builder()
                .websiteName(dto.getWebsiteName())
                .slogan(dto.getSlogan())
                .websiteUrl(dto.getWebsiteUrl())
                .contactEmail(dto.getContactEmail())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .companyName(dto.getCompanyName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .taxCode(dto.getTaxCode())
                .hotline(dto.getHotline())
                .language(dto.getLanguage())
                .timezone(dto.getTimezone())
                .currency(dto.getCurrency())
                .dateFormat(dto.getDateFormat())
                .build();
    }
}
