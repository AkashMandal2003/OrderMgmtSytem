package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.User;

import java.util.List;

public interface UserMgntDao {

    User createUser(User user);

    User finUserById(Integer id);

    List<User> getAllUsers();

    User updateUser(User user);

    User deleteUser(User user);

}
