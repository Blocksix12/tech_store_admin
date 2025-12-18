package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.SmtpSettings;

public interface SmtpSettingsService {
    SmtpSettings get();
    SmtpSettings save(SmtpSettings s);
}
