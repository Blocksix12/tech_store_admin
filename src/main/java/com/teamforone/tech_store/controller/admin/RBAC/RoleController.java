package com.teamforone.tech_store.controller.admin.RBAC;

import com.teamforone.tech_store.dto.request.RoleRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.service.admin.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/all")
    public List<Roles> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping("/add")
    public ResponseEntity<Response> addRole(@RequestBody RoleRequest roleRequest) {
        Response response = roleService.createRole(roleRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteRole(@PathVariable String id) {
        Response response = roleService.deleteRole(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
