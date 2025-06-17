package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.dto.permission.PermissionDTO;

/**
 * Mapper for converting between {@link Permission} entity and {@link PermissionDTO}.
 */
public class PermissionMapper {

    /**
     * Converts a {@link Permission} entity to a {@link PermissionDTO}.
     *
     * @param entity Permission entity (may be null)
     * @return PermissionDTO including organization summary, or null if input is null
     */
    public static PermissionDTO toDTO(Permission entity) {
        if (entity == null) return null;
        return PermissionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .organization(entity.getOrganization() != null
                        ? PermissionDTO.OrganizationSummary.builder()
                            .id(entity.getOrganization().getId())
                            .name(entity.getOrganization().getName())
                            .slug(entity.getOrganization().getSlug())
                            .build()
                        : null)
                .build();
    }
}