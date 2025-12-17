package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.RoleRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.repository.admin.RBAC.PermissionRepository;
import com.teamforone.tech_store.repository.admin.RBAC.RoleRepository;
import com.teamforone.tech_store.service.admin.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Response createRole(RoleRequest role) {
        String name = role.getName();
        String description = role.getDescription();
        var permissions = permissionRepository.findAllById(role.getPermission());

        Roles newRole = Roles.builder()
                .roleName(Roles.RoleName.toEnum(name))
                .description(description)
                .permissions(new HashSet<>(permissions))
                .build();

        roleRepository.save(newRole);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Role added successfully")
                .build();
    }

    @Override
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Response deleteRole(String id) {
        Roles existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return Response.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Role not found")
                    .build();
        }
        roleRepository.delete(existingRole);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
