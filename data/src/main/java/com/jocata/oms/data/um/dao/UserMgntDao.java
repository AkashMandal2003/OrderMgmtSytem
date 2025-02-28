package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.User;

import java.util.List;

public interface UserMgntDao {

    User createUser(User user);

    User finUserById(Integer id);

    User findByEmail(String email);

    User findUserByEmailAndPass(String email, String password);

    List<User> getAllUsers();

    User updateUser(User user);

    User softDeleteUser(User user);

    User permanentDeleteUser(User user);

}
