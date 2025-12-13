package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.SettingsGeneralDTO;
import com.teamforone.tech_store.model.SettingsGeneral;

import java.io.IOException;

public interface SettingsGeneralService {
    SettingsGeneralDTO get();
    SettingsGeneral saveSettings(SettingsGeneralDTO s) throws IOException;;
}
