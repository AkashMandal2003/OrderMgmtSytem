package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.RoleDao;
import com.jocata.oms.datamodel.um.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl implements RoleDao {

    private final HibernateConfig hibernateConfig;

    public RoleDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public Role createRole(Role role) {
        return hibernateConfig.saveEntity(role);
    }
}
