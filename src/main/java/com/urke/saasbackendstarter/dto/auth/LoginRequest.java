package com.urke.saasbackendstarter.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /**
     * User email address.
     */
    @Email
    @NotBlank
    private String email;

    /**
     * User password.
     */
    @NotBlank
    private String password;
}