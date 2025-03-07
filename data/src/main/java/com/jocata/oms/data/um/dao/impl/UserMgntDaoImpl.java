package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.UserMgntDao;
import com.jocata.oms.datamodel.um.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserMgntDaoImpl implements UserMgntDao {

    private final HibernateConfig hibernateConfig;

    public UserMgntDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return hibernateConfig.saveEntity(user);
    }

    @Override
    public User finUserById(Integer id) {
        return hibernateConfig.findEntityById(User.class, id);
    }

    @Override
    public User findByEmail(String email) {
        return hibernateConfig.findEntityByCriteria(User.class, "email", email);
    }

    @Override
    public User findUserByEmailAndPass(String email, String password) {
        Map<String,Object> params = new HashMap<>();
        params.put("email", email);
        params.put("passwordHash", password);
        return hibernateConfig.findEntityByMultipleCriteria(User.class,params);
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
    public User softDeleteUser(User user) {
        return hibernateConfig.softDeleteEntity(user);
    }

    @Override
    public User permanentDeleteUser(User user) {
        return hibernateConfig.deleteEntity(user,user.getUserId());
    }

}
