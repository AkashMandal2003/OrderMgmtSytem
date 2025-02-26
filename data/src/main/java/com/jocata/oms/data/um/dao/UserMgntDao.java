package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.User;

public interface UserMgntDao {

    User createUser(User user);

    User finUserById(Integer id);

    User updateUser(User user);

    void deleteUser(User user);
}
