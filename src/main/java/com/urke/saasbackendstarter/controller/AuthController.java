package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.auth.LoginRequest;
import com.urke.saasbackendstarter.dto.auth.LoginResponse;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenRequest;
import com.urke.saasbackendstarter.dto.auth.RefreshTokenResponse;
import com.urke.saasbackendstarter.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;

/**
 * REST controller for authentication and token management.
 */
@Tag(name = "Authentication", description = "Endpoints for user authentication, token refresh, and logout.")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication, token refresh, and logout.")
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT tokens.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login credentials (email and password)",
                required = true
            )
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Refresh JWT token",
        description = "Obtain new access token using refresh token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
        }
    )
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(
        summary = "Logout user",
        description = "Invalidate refresh token and logout user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(Principal principal, Locale locale) {
        if (principal != null) {
            authService.logout(principal.getName());
        }
        String msg = messageSource.getMessage("success.logout", null, locale);
        return ResponseEntity.ok(msg);
    }
}