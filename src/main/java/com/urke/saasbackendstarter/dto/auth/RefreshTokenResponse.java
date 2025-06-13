package com.urke.saasbackendstarter.dto.auth;

import lombok.Data;

/**
 * DTO for refresh token response.
 */
@Data
public class RefreshTokenResponse {
    /**
     * New JWT access token.
     */
    private String accessToken;

    /**
     * Same JWT refresh token.
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer").
     */
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}