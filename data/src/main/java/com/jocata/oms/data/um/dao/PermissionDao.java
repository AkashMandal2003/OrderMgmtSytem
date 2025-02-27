package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.Permission;

public interface PermissionDao {

    Permission createPermission(Permission permission);

    Permission getPermissionById(Integer id);

    Permission updatePermission(Permission permission);

}
