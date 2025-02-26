package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.UserMgntDao;
import com.jocata.oms.datamodel.um.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserMgntDaoImpl implements UserMgntDao {

    private final HibernateConfig hibernateConfig;

    public UserMgntDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public User createUser(User user) {
        return hibernateConfig.saveEntity(user);
    }

    @Override
    public User finUserById(Integer id) {
        return hibernateConfig.findEntityById(User.class, id);
    }

    @Override
    public User updateUser(User user) {
        return hibernateConfig.updateEntity(user);
    }

    @Override
    public void deleteUser(User user) {
        hibernateConfig.deleteEntity(user,user.getUserId());
    }
}
