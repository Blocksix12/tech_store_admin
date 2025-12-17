package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.PermissionRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Permission;
import com.teamforone.tech_store.repository.admin.RBAC.PermissionRepository;
import com.teamforone.tech_store.service.admin.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Response createPermission(PermissionRequest permission) {
        String name = permission.getName();
        String description = permission.getDescription();
        Permission newPermission = Permission.builder()
                .name(name)
                .description(description)
                .build();
        permissionRepository.save(newPermission);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Permission added successfully")
                .build();
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Response deletePermission(String id) {
        Permission existingPermission = permissionRepository.findById(id).orElse(null);
        if (existingPermission == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Permission not found")
                    .build();
        }
        permissionRepository.delete(existingPermission);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Permission deleted successfully")
                .build();
    }
}
