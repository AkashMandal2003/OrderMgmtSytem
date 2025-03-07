package com.jocata.oms.um.controller;

import com.jocata.oms.common.request.GenericRequestPayload;
import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.datamodel.um.form.SignInForm;
import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.um.jwt.JWTService;
import com.jocata.oms.um.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;

    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/public/register")
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


    @PostMapping("/public/upload")
    public ResponseEntity<Map<String, Object>> uploadUsers(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = userService.createUsersFromFile(file);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/{userId}")
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

    @GetMapping("/user")
    public ResponseEntity<GenericResponsePayload<?>> getUserByEmail(@RequestParam String email) {
        UserForm userByEmail = userService.getUserByEmail(email);
        return ResponseEntity.ok(new GenericResponsePayload<>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                userByEmail != null ? HttpStatus.FOUND.toString() : HttpStatus.NOT_FOUND.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                userByEmail != null ? userByEmail : "User not found"
        ));
    }


    @GetMapping("/public/sign-in")
    public ResponseEntity<GenericResponsePayload<?>> signIn(@RequestBody GenericRequestPayload<SignInForm> genericRequestPayload) {
        UserForm userByEmailAndPass = userService.getUserByEmail(
                genericRequestPayload.getData().getEmail(),
                genericRequestPayload.getData().getPassword());

        if (userByEmailAndPass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponsePayload<>(
                    UUID.randomUUID().toString(),
                    String.valueOf(Timestamp.from(Instant.now())),
                    HttpStatus.NOT_FOUND.toString(),
                    HttpStatus.Series.CLIENT_ERROR.toString(),
                    "User not found"
            ));
        }

        //Generate JWT Token with role
        String token = jwtService.generateToken(userByEmailAndPass.getEmail(),userByEmailAndPass.getRoles());

        //Return token + user details
        return ResponseEntity.ok(new GenericResponsePayload<>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                HttpStatus.OK.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                Map.of("user", userByEmailAndPass, "token", token)
        ));
    }

    @GetMapping("/admin/allusers")
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

    @PutMapping("/user/update-user")
    public ResponseEntity<GenericResponsePayload<UserForm>> updateUser(@RequestBody GenericRequestPayload<UserForm> genericRequestPayload) {
        UserForm userForm = userService.updateUser(genericRequestPayload.getData());
        return ResponseEntity.ok(new GenericResponsePayload<UserForm>(genericRequestPayload.getRequestId(),
                String.valueOf(Timestamp.from(Instant.now())),
                HttpStatus.OK.toString(),
                HttpStatus.Series.SUCCESSFUL.toString()
                , userForm
        ));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<GenericResponsePayload<String>> deleteUser(@PathVariable Integer userId, @RequestParam Boolean isHardDelete) {
        String message = userService.deleteUser(userId,isHardDelete);
        return ResponseEntity.ok(new GenericResponsePayload<String>(
                UUID.randomUUID().toString(),
                String.valueOf(Timestamp.from(Instant.now())),
                HttpStatus.OK.toString(),
                HttpStatus.Series.SUCCESSFUL.toString(),
                message
        ));
    }
}
