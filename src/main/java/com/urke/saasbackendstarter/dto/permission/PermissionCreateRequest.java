package com.urke.saasbackendstarter.dto.permission;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new permission scoped per organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionCreateRequest {
    @NotBlank(message = "Permission name must not be blank")
    private String name;

    @NotNull(message = "Organization id is required")
    private Long organizationId;
}