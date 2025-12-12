package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.SmtpSettings;
import com.teamforone.tech_store.repository.admin.SmtpSettingsRepository;
import com.teamforone.tech_store.service.admin.SmtpSettingsService;
import org.springframework.stereotype.Service;

@Service
public class SmtpSettingsServiceImpl implements SmtpSettingsService {
    private final SmtpSettingsRepository repo;

    public SmtpSettingsServiceImpl(SmtpSettingsRepository repo) {
        this.repo = repo;
    }

    @Override
    public SmtpSettings get() {
        return repo.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public SmtpSettings save(SmtpSettings s) {
        return repo.save(s);
    }
}
