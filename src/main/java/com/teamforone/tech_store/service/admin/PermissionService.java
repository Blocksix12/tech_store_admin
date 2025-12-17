package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.PermissionRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Permission;

import java.util.List;

public interface PermissionService {
    Response createPermission(PermissionRequest permission);
    List<Permission> getAllPermissions();
    Response deletePermission(String id);
}
