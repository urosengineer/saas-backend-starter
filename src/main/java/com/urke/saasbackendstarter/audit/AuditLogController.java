package com.urke.saasbackendstarter.audit;

import com.urke.saasbackendstarter.dto.AuditLogDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for exposing audit log endpoints.
 * All endpoints are restricted to administrators.
 */
@Tag(
    name = "Audit Logs",
    description = "Endpoints for viewing audit logs and tracking system actions. Only accessible to administrators."
)
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Retrieve a paginated list of audit logs, with optional filtering.
     *
     * @param page Page number (zero-based)
     * @param size Page size
     * @param action Optional action filter
     * @param entityType Optional entity type filter
     * @return Page of AuditLogDTO
     */
    @Operation(
        summary = "Get paged audit logs",
        description = "Retrieve a paged list of audit logs, optionally filtered by action or entity type. Only accessible to administrators.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based, default: 0)", example = "0"),
            @Parameter(name = "size", description = "Page size (default: 10)", example = "10"),
            @Parameter(name = "action", description = "Optional filter by action"),
            @Parameter(name = "entityType", description = "Optional filter by entity type")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Paged list of audit logs returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDTO> dtoPage = auditLogService
                .findPagedFiltered(action, entityType, pageable)
                .map(AuditLogMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }
}