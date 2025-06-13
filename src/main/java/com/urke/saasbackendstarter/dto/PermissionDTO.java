package com.urke.saasbackendstarter.dto;

import lombok.*;

/**
 * DTO for system permissions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private String name;
}