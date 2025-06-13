package com.urke.saasbackendstarter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for refresh token request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    /**
     * JWT refresh token.
     */
    @NotBlank
    private String refreshToken;
}