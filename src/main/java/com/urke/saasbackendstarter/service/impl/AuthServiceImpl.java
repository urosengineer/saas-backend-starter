package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.RefreshToken;
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
import com.urke.saasbackendstarter.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final MessageSource messageSource;

    // Refresh token validity duration (7 days)
    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isBlocked(email)) {
            long secondsLeft = loginAttemptService.getBlockTimeRemaining(email) / 1000;
            throw new AuthException(
                messageSource.getMessage("auth.login.blocked",
                        new Object[]{secondsLeft},
                        "Too many failed login attempts. Try again in {0} seconds.",
                        LocaleContextHolder.getLocale())
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            loginAttemptService.loginSucceeded(email);

            String accessToken = jwtTokenProvider.generateToken(userDetails);
            String refreshToken = createRefreshToken(
                userRepository.findByEmailAndDeletedFalse(email)
                        .orElseThrow(() -> new AuthException(messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())))
            );

            return new LoginResponse(accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(email);
            throw new AuthException(messageSource.getMessage("auth.invalid.credentials", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestToken = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new AuthException(
                    messageSource.getMessage("auth.refresh.invalid", null, LocaleContextHolder.getLocale())
                ));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthException(
                messageSource.getMessage("auth.refresh.invalid", null, LocaleContextHolder.getLocale())
            );
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .toArray(String[]::new))
                .build();

        String newAccessToken = jwtTokenProvider.generateToken(userDetails);

        return new RefreshTokenResponse(newAccessToken, requestToken);
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new AuthException(
                        messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())
                ));
        refreshTokenRepository.deleteByUser(user);
        loginAttemptService.loginSucceeded(email);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
