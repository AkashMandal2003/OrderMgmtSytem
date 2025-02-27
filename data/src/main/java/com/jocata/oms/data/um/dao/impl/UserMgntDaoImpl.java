package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.UserMgntDao;
import com.jocata.oms.datamodel.um.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<User> getAllUsers() {
        return hibernateConfig.loadEntitiesByCriteria(User.class);
    }

    @Override
    public User updateUser(User user) {
        return hibernateConfig.updateEntity(user);
    }

    @Override
    public User deleteUser(User user) {
        return hibernateConfig.softDeleteEntity(user);
    }
}
