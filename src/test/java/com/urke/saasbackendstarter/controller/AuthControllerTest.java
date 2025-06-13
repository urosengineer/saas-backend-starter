package com.urke.saasbackendstarter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urke.saasbackendstarter.dto.auth.LoginRequest;
import com.urke.saasbackendstarter.dto.auth.LoginResponse;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenRequest;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenResponse;
import com.urke.saasbackendstarter.exception.AuthException;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
import com.urke.saasbackendstarter.security.JwtAuthenticationFilter;
import com.urke.saasbackendstarter.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private MessageSource messageSource;

    // Mocking security dependencies to avoid context load errors
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/login - Success")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
        LoginResponse loginResponse = new LoginResponse("access-token", "refresh-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongpassword");

        when(authService.login(any())).thenThrow(new AuthException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());

        verify(authService, times(1)).login(any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh - Success")
    void testRefreshTokenSuccess() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-refresh-token");
        RefreshTokenResponse refreshResponse = new RefreshTokenResponse("new-access-token", "valid-refresh-token");

        when(authService.refreshToken(any())).thenReturn(refreshResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"));

        verify(authService, times(1)).refreshToken(any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh - Invalid token")
    void testRefreshTokenInvalid() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-refresh-token");

        when(authService.refreshToken(any())).thenThrow(new AuthException("Invalid refresh token"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());

        verify(authService, times(1)).refreshToken(any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - Success")
    void testLogoutSuccess() throws Exception {
        String logoutMsg = "Logout successful";
        when(messageSource.getMessage(eq("success.logout"), isNull(), any(Locale.class))).thenReturn(logoutMsg);

        mockMvc.perform(post("/api/v1/auth/logout")
                .principal(() -> "user@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().string(logoutMsg));

        verify(authService, times(1)).logout("user@example.com");
        verify(messageSource, times(1)).getMessage(eq("success.logout"), isNull(), any(Locale.class));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - Anonymous user")
    void testLogoutAnonymous() throws Exception {
        String logoutMsg = "Logout successful";
        when(messageSource.getMessage(eq("success.logout"), isNull(), any(Locale.class))).thenReturn(logoutMsg);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string(logoutMsg));

        verify(authService, times(0)).logout(any());
        verify(messageSource, times(1)).getMessage(eq("success.logout"), isNull(), any(Locale.class));
    }
}