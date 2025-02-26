package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.PermissionDao;
import com.jocata.oms.datamodel.um.entity.Permission;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionDaoImpl implements PermissionDao {

    private final HibernateConfig hibernateConfig;

    public PermissionDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public Permission createPermission(Permission permission) {
        return hibernateConfig.saveEntity(permission);
    }
}
