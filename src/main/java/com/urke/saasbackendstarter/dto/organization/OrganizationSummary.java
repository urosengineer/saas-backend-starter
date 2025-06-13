package com.urke.saasbackendstarter.dto.organization;

import lombok.*;

/**
 * Summary DTO for organization listing and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSummary {
    private Long id;
    private String name;
    private String slug;
}