package com.urke.saasbackendstarter.dto.role;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * Request DTO for updating an existing user role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateRequest {
    @NotNull
    private Long id;

    @NotBlank(message = "Role name must not be blank")
    private String name;

    private Set<Long> permissionIds;
}
