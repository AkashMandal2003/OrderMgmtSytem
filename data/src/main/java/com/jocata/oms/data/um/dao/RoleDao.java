package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.Role;

public interface RoleDao {

    Role createRole(Role role);

    Role getRoleById(Integer id);

    Role updateRole(Role role);

}
