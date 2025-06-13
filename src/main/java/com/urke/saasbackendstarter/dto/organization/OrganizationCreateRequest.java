package com.urke.saasbackendstarter.dto.organization;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Request DTO for creating a new organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationCreateRequest {
    @NotBlank
    @Size(max = 64)
    private String name;
}