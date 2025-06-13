package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * REST controller for password reset endpoints.
 */
@Tag(
    name = "Password Reset",
    description = "Endpoints for requesting and confirming password reset via email."
)
@RestController
@RequestMapping("/api/v1/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final MessageSource messageSource;

    /**
     * Request password reset instructions for a given email.
     * Always returns a generic message to avoid user enumeration.
     */
    @Operation(
        summary = "Request password reset",
        description = "Send password reset instructions to the given email if a user exists. Always returns a generic message for security.",
        requestBody = @RequestBody(
            description = "Email address for which to request password reset",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Reset instructions sent if email exists"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
        }
    )
    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@Valid @RequestBody RequestResetDto dto, Locale locale) {
        passwordResetService.createResetToken(dto.getEmail());
        String msg = messageSource.getMessage("reset.instructions.sent", null, locale);
        return ResponseEntity.ok().body(msg);
    }

    /**
     * Confirm password reset using token and new password.
     */
    @Operation(
        summary = "Confirm password reset",
        description = "Reset the user's password using a valid reset token and new password.",
        requestBody = @RequestBody(
            description = "Reset token and new password",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Password has been reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token, or invalid input")
        }
    )
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReset(@Valid @RequestBody ConfirmResetDto dto, Locale locale) {
        passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());
        String msg = messageSource.getMessage("password.reset.success", null, locale);
        return ResponseEntity.ok().body(msg);
    }

    /**
     * DTO for password reset request.
     */
    @Data
    public static class RequestResetDto {
        @Email
        @NotBlank
        private String email;
    }

    /**
     * DTO for password reset confirmation.
     */
    @Data
    public static class ConfirmResetDto {
        @NotBlank
        private String token;
        @NotBlank
        private String newPassword;
    }
}