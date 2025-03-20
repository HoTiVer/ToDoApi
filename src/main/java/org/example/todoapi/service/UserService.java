package org.example.todoapi.service;

import org.example.todoapi.DTO.LoginResponse;
import org.example.todoapi.DTO.RegisterResponse;
import org.example.todoapi.Repository.UserRepository;
import org.example.todoapi.DTO.UserDTO;
import org.example.todoapi.entity.User;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

import java.util.Map;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Map<String, Object>> register(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.info("register method: Email already exists: {}", userDTO.getEmail());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Email already exists"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok()
                .body(Map.of("success", true,
                        "message", "Success register", "token", token));
    }

    public LoginResponse login(UserDTO userDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword())
            );

            if (authentication.isAuthenticated()) {
                log.info("login method: User logged in: {}", userDTO.getEmail());
                String token = jwtService.generateToken(userDTO.getEmail());
                return new LoginResponse(true, token);
            }
        }
        catch (Exception e) {
            log.warn("login method: Failed to login: {}", userDTO.getEmail());
            return new LoginResponse(false, null);
        }

        return new LoginResponse(false, null);
    }
}
