package com.urke.saasbackendstarter.dto.auth;

import lombok.Data;

/**
 * DTO for user login response containing JWT tokens.
 */
@Data
public class LoginResponse {
    /**
     * JWT access token.
     */
    private String accessToken;

    /**
     * JWT refresh token.
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer").
     */
    private String tokenType = "Bearer";

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}