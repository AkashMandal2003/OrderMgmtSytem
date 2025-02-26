package com.jocata.oms.um.controller;

import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.um.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserForm registerUser(@RequestBody UserForm user) {
        return userService.registerUser(user);
    }
}
