package com.urke.saasbackendstarter.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for representing audit log entries.
 *
 * Encapsulates information about system actions for auditing and tracking purposes,
 * including the action performed, entity type, timestamp, and the actor's email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String message;
    private LocalDateTime timestamp;
    private String actorEmail;
}