package com.jocata.oms.form;


import java.util.Set;

public class RoleForm {

    private String roleId;

    private String roleName;

    private Set<PermissionForm> permissions;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<PermissionForm> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionForm> permissions) {
        this.permissions = permissions;
    }
}
