package com.urke.saasbackendstarter.dto.permission;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating an existing permission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionUpdateRequest {
    @NotNull
    private Long id;

    @NotBlank(message = "Permission name must not be blank")
    private String name;
}