package com.example.librarymanagement.controller;

import com.example.librarymanagement.controller.request.AuthenticationRequest;
import com.example.librarymanagement.controller.request.RegisterRequest;
import com.example.librarymanagement.controller.request.ResetPasswordRequest;
import com.example.librarymanagement.controller.response.AuthenticationResponse;
import com.example.librarymanagement.service.Imp.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(AuthController.class)
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@Import({JwtService.class, SecurityConfig.class, ApplicationConfig.class})
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthControllerTest {
    @MockBean
    private AuthenticationService authenticationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register() throws Exception {
        RegisterRequest request = new RegisterRequest();

        AuthenticationResponse response = new AuthenticationResponse();

        given(authenticationService.register(any(RegisterRequest.class))).willReturn(response);

        mockMvc.perform(post("/library/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();

        AuthenticationResponse response = new AuthenticationResponse();
        given(authenticationService.authenticate(any(AuthenticationRequest.class))).willReturn(response);

        mockMvc.perform(post("/library/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword() throws Exception {
        String username = "test_user";
        String oldPassword = "old_password";
        String newPassword = "new_password";

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, oldPassword);
        ResetPasswordRequest request = new ResetPasswordRequest(authenticationRequest, newPassword);

        doNothing().when(authenticationService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/library/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}