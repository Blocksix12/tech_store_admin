package com.teamforone.tech_store.repository.admin;


import com.teamforone.tech_store.model.SettingsGeneral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsGeneralRepository extends JpaRepository<SettingsGeneral, String> {
    Optional<SettingsGeneral> findTopByOrderByIdAsc();
}
