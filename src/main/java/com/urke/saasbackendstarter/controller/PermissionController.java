package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.PermissionDTO;
import com.urke.saasbackendstarter.mapper.PermissionMapper;
import com.urke.saasbackendstarter.service.PermissionService;
import com.urke.saasbackendstarter.domain.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * REST controller for managing system permissions.
 * <p>
 * Exposes endpoints for retrieving available permissions.
 * Access is restricted to users with the ADMIN role.
 */
@Tag(
    name = "Permissions",
    description = "Endpoints for listing system permissions. Only available to administrators."
)
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;
    private final MessageSource messageSource;

    /**
     * Retrieve a list of all permissions in the system.
     * Only accessible to users with the ADMIN role.
     *
     * @param locale The user's locale, used for localized messages.
     * @return ResponseEntity containing a list of permissions and an optional message.
     */
    @Operation(
        summary = "Get all permissions",
        description = "Retrieve a list of all permissions in the system. Only accessible to users with the ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Permissions listed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges")
        }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionsResponse> getAllPermissions(Locale locale) {
        List<Permission> permissions = permissionService.findAll();
        if (permissions.isEmpty()) {
            String msg = messageSource.getMessage("permissions.empty", null, locale);
            return ResponseEntity.ok(new PermissionsResponse(List.of(), msg));
        }
        List<PermissionDTO> dtos = permissions.stream()
                .map(permissionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PermissionsResponse(dtos, null));
    }

    /**
     * Immutable response record for permission API results.
     *
     * @param permissions List of PermissionDTOs.
     * @param message     Optional message (e.g. if the list is empty).
     */
    public static record PermissionsResponse(List<PermissionDTO> permissions, String message) {}
}