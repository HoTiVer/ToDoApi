package org.example.todoapi.Service;

import org.example.todoapi.DTO.LoginResponse;
import org.example.todoapi.DTO.UserDTO;
import org.example.todoapi.Repository.UserRepository;
import org.example.todoapi.service.JwtService;
import org.example.todoapi.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        userDTO = new UserDTO("name", "email", "password");
    }

    @Test
    public void registerNewUser_SuccessRegister() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(jwtService.generateToken(userDTO.getEmail())).thenReturn("test token");

        ResponseEntity<Map<String, Object>> responseEntity = userService.register(userDTO);


        Assertions.assertEquals(200, responseEntity.getStatusCode().value());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("test token", responseEntity.getBody().get("token"));
        Assertions.assertTrue((Boolean) responseEntity.getBody().get("success"));
    }
    @Test
    public void registerNewUser_FailRegister() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        ResponseEntity<Map<String, Object>> responseEntity = userService.register(userDTO);

        Assertions.assertEquals(400, responseEntity.getStatusCode().value());
        Assertions.assertFalse((Boolean) responseEntity.getBody().get("success"));
    }
    @Test
    public void login_SuccessLogin() {
        when(authenticationManager
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(userDTO.getEmail())).thenReturn("test token");

        LoginResponse loginResponse = userService.login(userDTO);

        Assertions.assertTrue(loginResponse.getSuccess());
        Assertions.assertEquals("test token", loginResponse.getToken());

    }
    @Test
    public void login_FailLogin() {
        LoginResponse loginResponse = userService.login(userDTO);

        Assertions.assertFalse(loginResponse.getSuccess());
        Assertions.assertNull(loginResponse.getToken());
    }
}
