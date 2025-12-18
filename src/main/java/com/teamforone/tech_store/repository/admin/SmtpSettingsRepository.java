package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.SmtpSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmtpSettingsRepository extends JpaRepository<SmtpSettings, String> {
}
