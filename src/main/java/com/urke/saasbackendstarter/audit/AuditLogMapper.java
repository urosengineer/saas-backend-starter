package com.urke.saasbackendstarter.audit;

import com.urke.saasbackendstarter.dto.AuditLogDTO;

/**
 * Utility class for mapping AuditLog entities to AuditLogDTO.
 */
public class AuditLogMapper {

    /**
     * Maps an AuditLog entity to an AuditLogDTO.
     *
     * @param entity AuditLog entity
     * @return AuditLogDTO instance
     */
    public static AuditLogDTO toDTO(AuditLog entity) {
        if (entity == null) return null;
        return AuditLogDTO.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .message(entity.getMessage())
                .timestamp(entity.getTimestamp())
                .actorEmail(entity.getActorEmail())
                .build();
    }

    private AuditLogMapper() {
        // Utility class
    }
}