package com.teamforone.tech_store.repository.admin.RBAC;

import com.teamforone.tech_store.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}
