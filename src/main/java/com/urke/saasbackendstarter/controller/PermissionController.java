package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.permission.PermissionDTO;
import com.urke.saasbackendstarter.dto.permission.PermissionCreateRequest;
import com.urke.saasbackendstarter.mapper.PermissionMapper;
import com.urke.saasbackendstarter.service.PermissionService;
import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing permissions per organization.
 */
@Tag(
    name = "Permissions",
    description = "Endpoints for managing permissions per organization."
)
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final OrganizationRepository organizationRepository;

    @Operation(
        summary = "Get all permissions for an organization",
        description = "Retrieve a list of all permissions for a given organization (tenant).",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Permissions listed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissionsForOrganization(
            @PathVariable Long organizationId) {
        List<Permission> permissions = permissionService.findAllByOrganizationId(organizationId);
        List<PermissionDTO> dtos = permissions.stream()
                .map(PermissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(
        summary = "Create a new permission for an organization",
        description = "Create a new permission for a specific organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Permission created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Permission already exists")
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionDTO> createPermission(
            @Valid @RequestBody PermissionCreateRequest request) {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        if (permissionService.findByNameAndOrganizationId(request.getName(), organization.getId()).isPresent()) {
            throw new IllegalArgumentException("Permission already exists for this organization");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .organization(organization)
                .build();

        Permission saved = permissionService.save(permission);
        return ResponseEntity.ok(PermissionMapper.toDTO(saved));
    }
}