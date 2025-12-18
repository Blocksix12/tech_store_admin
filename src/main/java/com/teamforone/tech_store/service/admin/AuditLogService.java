package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.AuditLog;

import java.util.List;

public interface AuditLogService {
    List<AuditLog> list();
    AuditLog save(AuditLog log);
    void clearAll();
}
