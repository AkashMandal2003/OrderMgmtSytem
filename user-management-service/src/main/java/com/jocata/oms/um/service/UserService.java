package com.jocata.oms.um.service;

import com.jocata.oms.datamodel.um.form.UserForm;

import java.util.List;

public interface UserService {

    UserForm registerUser(UserForm user);

    UserForm getUserById(Integer userId);

    UserForm getUserByEmail(String email);

    UserForm getUserByEmail(String email, String password);

    List<UserForm> getAllUsers();

    UserForm updateUser(UserForm user);

    String deleteUser(Integer userId, Boolean isHardDelete);
}
