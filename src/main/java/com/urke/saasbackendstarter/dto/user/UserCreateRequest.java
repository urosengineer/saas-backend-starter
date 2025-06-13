package com.urke.saasbackendstarter.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for user registration (input).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 50)
    private String fullName;

    @NotNull(message = "Organization ID is required")
    private Long organizationId;
}