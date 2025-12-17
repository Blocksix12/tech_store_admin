package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.RoleRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Roles;

import java.util.List;

public interface RoleService {
    Response createRole(RoleRequest role);
    List<Roles> getAllRoles();
    Response deleteRole(String id);
}
