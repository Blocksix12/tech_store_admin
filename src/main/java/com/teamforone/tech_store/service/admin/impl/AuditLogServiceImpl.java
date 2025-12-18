package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.AuditLog;
import com.teamforone.tech_store.repository.admin.AuditLogRepository;
import com.teamforone.tech_store.service.admin.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository repo;

    public AuditLogServiceImpl(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<AuditLog> list(){
        return repo.findAll();
    }

    @Override
    public AuditLog save(AuditLog log){
        return repo.save(log);
    }

    @Override
    public void clearAll(){
        repo.deleteAll();
    }
}
