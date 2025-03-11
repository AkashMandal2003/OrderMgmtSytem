package com.jocata.oms.um.controller;

import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.um.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth/user")
    public ResponseEntity<GenericResponsePayload<?>> getUserByEmail(@RequestParam String username) {
        UserForm userByEmail = userService.getUserByEmail(username);
        return ResponseEntity.ok(new GenericResponsePayload<>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                userByEmail != null ? HttpStatus.FOUND.toString() : HttpStatus.NOT_FOUND.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                userByEmail != null ? userByEmail : "User not found"
        ));
    }

}
