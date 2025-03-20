package org.example.todoapi.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.todoapi.DTO.LoginResponse;
import org.example.todoapi.DTO.UserDTO;
import org.example.todoapi.config.SecurityConfig;
import org.example.todoapi.controller.AuthController;
import org.example.todoapi.service.JwtService;
import org.example.todoapi.service.MyUserDetailsService;
import org.example.todoapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MyUserDetailsService userDetailsService;

    UserDTO userDTO;

    @BeforeEach
    public void setup() {
        userDTO = new UserDTO("name", "email", "password");
    }

    @Test
    public void registerNewUser_SuccessRegistration() throws Exception {

        ResponseEntity<Map<String, Object>> responseEntity =
                ResponseEntity.ok()
                        .body(Map.of("success", true,
                                "message", "Success register",
                                "token", "test token"));

        when(userService.register(Mockito.any(UserDTO.class)))
                .thenReturn(responseEntity);

        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.success").value(true));

    }

    @Test
    public void registerNewUser_FailRegistration() throws Exception {
        ResponseEntity<Map<String, Object>> responseEntity =
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false,
                                "message", "Email already exists"));;

        when(userService.register(Mockito.any(UserDTO.class)))
                .thenReturn(responseEntity);

        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.success").value(false));
    }

    @Test
    public void login_SuccessLogin() throws Exception {
        LoginResponse loginResponse = new LoginResponse(true, "test token");

        when(userService.login(Mockito.any(UserDTO.class)))
                .thenReturn(loginResponse);

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.success").value(true));
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.token").value("test token"));
    }

    @Test
    public void login_FailLogin() throws Exception {
        LoginResponse loginResponse = new LoginResponse(false, null);

        when(userService.login(Mockito.any(UserDTO.class)))
                .thenReturn(loginResponse);

        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.success").value(false));
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.token").isEmpty());
    }
}
