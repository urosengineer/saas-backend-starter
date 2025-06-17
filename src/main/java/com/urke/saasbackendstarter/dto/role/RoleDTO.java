package com.urke.saasbackendstarter.dto.role;

import com.urke.saasbackendstarter.dto.permission.PermissionDTO;

import lombok.*;

import java.util.Set;

/**
 * DTO representing a user role, including its permissions and organization context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private String name;
    private Set<PermissionDTO> permissions;
    private OrganizationSummary organization;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrganizationSummary {
        private Long id;
        private String name;
        private String slug;
    }
}