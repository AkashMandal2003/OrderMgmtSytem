package com.jocata.oms.um.controller;

import com.jocata.oms.common.request.GenericRequestPayload;
import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.um.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponsePayload<UserForm>> registerUser(@RequestBody GenericRequestPayload<UserForm> genericRequestPayload) {
        UserForm userForm = userService.registerUser(genericRequestPayload.getData());
        return new ResponseEntity<GenericResponsePayload<UserForm>>(
                new GenericResponsePayload<UserForm>(genericRequestPayload.getRequestId(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.CREATED.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString()
                        , userForm),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponsePayload<?>> getUser(@PathVariable Integer userId) {
        UserForm userById = userService.getUserById(userId);
        return ResponseEntity.ok(new GenericResponsePayload<>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                userById != null ? HttpStatus.FOUND.toString() : HttpStatus.NOT_FOUND.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                userById != null ? userById : "User not found"
        ));
    }

    @GetMapping
    public ResponseEntity<GenericResponsePayload<List<UserForm>>> getAllUsers() {
        List<UserForm> users = userService.getAllUsers();
        return ResponseEntity.ok(
                new GenericResponsePayload<List<UserForm>>(
                        UUID.randomUUID().toString(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.FOUND.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString(),
                        users
                )
        );
    }

    @PutMapping("/update-user")
    public ResponseEntity<GenericResponsePayload<UserForm>> updateUser(@RequestBody GenericRequestPayload<UserForm> genericRequestPayload) {
        UserForm userForm = userService.updateUser(genericRequestPayload.getData());
        return ResponseEntity.ok(new GenericResponsePayload<UserForm>(genericRequestPayload.getRequestId(),
                String.valueOf(Timestamp.from(Instant.now())),
                HttpStatus.OK.toString(),
                HttpStatus.Series.SUCCESSFUL.toString()
                , userForm
        ));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<GenericResponsePayload<String>> deleteUser(@PathVariable Integer userId) {
        String message = userService.deleteUser(userId);
        return ResponseEntity.ok(new GenericResponsePayload<String>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                HttpStatus.OK.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                message
        ));
    }
}
