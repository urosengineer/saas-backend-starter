package com.urke.saasbackendstarter.dto.role;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * Request DTO for creating a new user role, scoped per organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCreateRequest {
    @NotBlank(message = "Role name must not be blank")
    private String name;

    private Set<Long> permissionIds;

    @NotNull(message = "Organization id is required")
    private Long organizationId;
}
