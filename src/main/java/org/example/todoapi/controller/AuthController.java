package org.example.todoapi.controller;

import org.example.todoapi.DTO.LoginResponse;
import org.example.todoapi.DTO.RegisterResponse;
import org.example.todoapi.DTO.UserDTO;
import org.example.todoapi.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.logging.Logger;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

}
