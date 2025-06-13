package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.RefreshToken;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.dto.auth.LoginRequest;
import com.urke.saasbackendstarter.dto.auth.LoginResponse;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenRequest;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenResponse;
import com.urke.saasbackendstarter.exception.AuthException;
import com.urke.saasbackendstarter.repository.RefreshTokenRepository;
import com.urke.saasbackendstarter.repository.UserRepository;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
import com.urke.saasbackendstarter.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private LoginAttemptService loginAttemptService;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        // Test role
        role = new Role();
        role.setName("USER");

        // Test user
        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(role));
        user.setDeleted(false);
    }

    @Test
    void login_successful() {
        LoginRequest request = new LoginRequest("user@example.com", "Secret123");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();

        when(loginAttemptService.isBlocked(user.getEmail())).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmailAndDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any())).thenReturn("access.jwt.token");
        doNothing().when(loginAttemptService).loginSucceeded(user.getEmail());
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access.jwt.token");
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(loginAttemptService).loginSucceeded(user.getEmail());
    }

    @Test
    void login_blockedUser_shouldThrowException() {
        LoginRequest request = new LoginRequest("user@example.com", "Secret123");
        when(loginAttemptService.isBlocked(user.getEmail())).thenReturn(true);
        when(loginAttemptService.getBlockTimeRemaining(user.getEmail())).thenReturn(60000L);
        when(messageSource.getMessage(any(), any(), any(), any())).thenReturn("Blocked!");

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("Blocked!");
        verify(loginAttemptService, never()).loginSucceeded(anyString());
    }

    @Test
    void login_invalidCredentials_shouldThrowException() {
        LoginRequest request = new LoginRequest("user@example.com", "badpass");
        when(loginAttemptService.isBlocked(user.getEmail())).thenReturn(false);
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        when(messageSource.getMessage(eq("auth.invalid.credentials"), any(), any()))
            .thenReturn("Invalid credentials");
        doNothing().when(loginAttemptService).loginFailed(user.getEmail());

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("Invalid credentials");
        verify(loginAttemptService).loginFailed(user.getEmail());
    }

    @Test
    void refreshToken_successful() {
        String refreshTokenValue = "refresh-token";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(Instant.now().plusSeconds(600))
                .user(user)
                .build();
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("new.access.token");

        RefreshTokenResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new.access.token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshTokenValue);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void refreshToken_expired_shouldThrowException() {
        String refreshTokenValue = "expired-token";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(Instant.now().minusSeconds(10))
                .user(user)
                .build();
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(messageSource.getMessage(eq("auth.refresh.invalid"), any(), any())).thenReturn("Refresh token invalid");

        assertThatThrownBy(() -> authService.refreshToken(request))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("Refresh token invalid");
    }

    @Test
    void logout_shouldDeleteRefreshTokenAndUnblockUser() {
        when(userRepository.findByEmailAndDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);
        doNothing().when(loginAttemptService).loginSucceeded(user.getEmail());

        assertThatCode(() -> authService.logout(user.getEmail())).doesNotThrowAnyException();

        verify(refreshTokenRepository).deleteByUser(user);
        verify(loginAttemptService).loginSucceeded(user.getEmail());
    }

    @Test
    void logout_userNotFound_shouldThrowException() {
        when(userRepository.findByEmailAndDeletedFalse(user.getEmail())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("user.notfound"), any(), any())).thenReturn("User not found");

        assertThatThrownBy(() -> authService.logout(user.getEmail()))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("User not found");
        verify(refreshTokenRepository, never()).deleteByUser(any());
    }
}