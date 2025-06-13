package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.dto.PermissionDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Permission entity and PermissionDTO.
 * 
 * Provides utility methods for mapping single objects and collections.
 * Promotes clear separation of domain and transfer layers.
 */
@Component
public class PermissionMapper {

    /**
     * Converts a Permission entity to a PermissionDTO.
     *
     * @param entity Permission entity (may be null)
     * @return PermissionDTO or null if input is null
     */
    public PermissionDTO toDto(Permission entity) {
        if (entity == null) return null;
        return PermissionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    /**
     * Converts a PermissionDTO to a Permission entity.
     *
     * @param dto PermissionDTO (may be null)
     * @return Permission entity or null if input is null
     */
    public Permission toEntity(PermissionDTO dto) {
        if (dto == null) return null;
        return Permission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

}