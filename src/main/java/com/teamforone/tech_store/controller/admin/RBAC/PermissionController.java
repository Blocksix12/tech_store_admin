package com.teamforone.tech_store.controller.admin.RBAC;

import com.teamforone.tech_store.dto.request.PermissionRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Permission;
import com.teamforone.tech_store.service.admin.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
@Slf4j
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/all")
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PostMapping("/add")
    public ResponseEntity<Response> AddPermisson(@RequestBody PermissionRequest permissionRequest) {
        Response response = permissionService.createPermission(permissionRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deletePermission(@PathVariable String id) {
        Response response = permissionService.deletePermission(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
