package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}
