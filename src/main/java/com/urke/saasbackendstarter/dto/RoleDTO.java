package com.urke.saasbackendstarter.dto;

import lombok.*;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user role including permissions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;

    @NotBlank(message = "Role name must not be blank")
    private String name;

    private Set<PermissionDTO> permissions;
}