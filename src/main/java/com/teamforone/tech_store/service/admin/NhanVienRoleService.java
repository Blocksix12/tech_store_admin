package com.teamforone.tech_store.service.admin;

import java.util.List;

public interface NhanVienRoleService {
    void assignRoles(String nhanVienId, List<String> roleIds);
    void removeRole(String nhanVienId, String roleId);
    void clearAllRoles(String nhanVienId);
    void replaceRoles(String nhanVienId, List<String> roleIds);
    boolean hasRole(String nhanVienId, String roleId);
}
