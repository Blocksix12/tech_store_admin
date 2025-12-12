package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.SettingsGeneral;
import com.teamforone.tech_store.repository.admin.SettingsGeneralRepository;
import com.teamforone.tech_store.service.admin.SettingsGeneralService;
import org.springframework.stereotype.Service;

@Service
public class SettingsGeneralServiceImpl implements SettingsGeneralService {
    private final SettingsGeneralRepository repo;

    public SettingsGeneralServiceImpl(SettingsGeneralRepository repo) {
        this.repo = repo;
    }


    @Override
    public SettingsGeneral get() {
        return repo.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public SettingsGeneral save(SettingsGeneral s) {
        return repo.save(s);
    }
}
