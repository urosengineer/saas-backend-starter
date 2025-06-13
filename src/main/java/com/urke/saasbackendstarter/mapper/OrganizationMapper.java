package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import com.urke.saasbackendstarter.dto.organization.OrganizationSummary;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Organization entity and DTOs.
 * Provides utility methods for mapping between entity and summary/request DTOs.
 */
@Component
public class OrganizationMapper {

    /**
     * Maps Organization entity to OrganizationSummary DTO.
     *
     * @param entity Organization entity
     * @return OrganizationSummary DTO or null if input is null
     */
    public OrganizationSummary toSummary(Organization entity) {
        if (entity == null) return null;
        return OrganizationSummary.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .build();
    }

    /**
     * Maps OrganizationCreateRequest DTO to Organization entity.
     *
     * @param dto OrganizationCreateRequest DTO
     * @return Organization entity or null if input is null
     */
    public Organization toEntity(OrganizationCreateRequest dto) {
        if (dto == null) return null;
        return Organization.builder()
                .name(dto.getName())
                // slug and deleted fields are handled in the service
                .build();
    }
}