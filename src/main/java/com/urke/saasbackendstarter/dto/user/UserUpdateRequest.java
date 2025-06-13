package com.urke.saasbackendstarter.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for updating user profile (input).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 50)
    private String fullName;

    @Size(min = 8, max = 64)
    private String newPassword;
}