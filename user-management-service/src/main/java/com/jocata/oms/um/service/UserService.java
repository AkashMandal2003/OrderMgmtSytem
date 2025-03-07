package com.jocata.oms.um.service;

import com.jocata.oms.datamodel.um.form.UserForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserForm registerUser(UserForm user);

    Map<String, Object> createUsersFromFile(MultipartFile file);

    UserForm getUserById(Integer userId);

    UserForm getUserByEmail(String email);

    UserForm getUserByEmail(String email, String password);

    List<UserForm> getAllUsers();

    UserForm updateUser(UserForm user);

    String deleteUser(Integer userId, Boolean isHardDelete);
}
