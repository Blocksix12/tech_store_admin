package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.SettingsGeneral;
import com.teamforone.tech_store.service.admin.SettingsGeneralService;
import com.teamforone.tech_store.service.admin.impl.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/settings")
public class UploadController {
    private final FileStorageService storage;
    private final SettingsGeneralService generalService;

    public UploadController(FileStorageService storage, SettingsGeneralService generalService) {
        this.storage = storage;
        this.generalService = generalService;
    }

    @PostMapping("/upload/logo")
    public String uploadLogo(@RequestParam("logoFile") MultipartFile logoFile,
                             @RequestParam(value = "faviconFile", required = false) MultipartFile faviconFile) throws Exception {
        SettingsGeneral s = generalService.get();
        if (s == null) s = new SettingsGeneral();
        if (logoFile != null && !logoFile.isEmpty()) {
            String p = storage.store(logoFile, "logo");
            s.setLogoUrl(p);
        }
        if (faviconFile != null && !faviconFile.isEmpty()) {
            String p = storage.store(faviconFile, "favicon");
            s.setFaviconUrl(p);
        }
        generalService.save(s);
        return "redirect:/admin/settings?tab=general&uploaded";
    }



}
