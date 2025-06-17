package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.role.RoleDTO;
import com.urke.saasbackendstarter.dto.role.RoleCreateRequest;
import com.urke.saasbackendstarter.dto.role.RoleUpdateRequest;
import com.urke.saasbackendstarter.mapper.RoleMapper;
import com.urke.saasbackendstarter.repository.PermissionRepository;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.service.RoleService;
import com.urke.saasbackendstarter.exception.PermissionNotFoundException;
import com.urke.saasbackendstarter.exception.OrganizationNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing user roles and permissions per organization (tenant).
 */
@Tag(
    name = "Roles",
    description = "Endpoints for listing and managing user roles per organization."
)
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionRepository permissionRepository;
    private final OrganizationRepository organizationRepository;

    @Operation(
        summary = "Get all roles for an organization",
        description = "Retrieve all user roles for a given organization (tenant).",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Roles listed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDTO>> getAllRolesForOrganization(
            @PathVariable Long organizationId) {
        List<Role> roles = roleService.findAllByOrganizationId(organizationId);
        List<RoleDTO> roleDTOs = roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }

    @Operation(
        summary = "Create a new role for an organization",
        description = "Create a new user role for a specific organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Role already exists")
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> createRole(
            @Valid @RequestBody RoleCreateRequest request) {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found: " + request.getOrganizationId()));

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions = request.getPermissionIds().stream()
                .map(pid -> permissionRepository.findById(pid)
                    .orElseThrow(() -> new PermissionNotFoundException("Permission not found: " + pid)))
                .collect(Collectors.toSet());
        }

        Role role = Role.builder()
                .name(request.getName())
                .permissions(permissions)
                .organization(organization)
                .build();

        Role saved = roleService.save(role);
        return ResponseEntity.ok(RoleMapper.toDTO(saved));
    }
}