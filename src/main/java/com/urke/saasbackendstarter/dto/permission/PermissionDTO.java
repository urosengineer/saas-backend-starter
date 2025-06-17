package com.urke.saasbackendstarter.dto.permission;

import lombok.*;

/**
 * DTO for system permission, including organization info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private String name;
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