package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.dto.RoleDTO;
import com.urke.saasbackendstarter.mapper.RoleMapper;
import com.urke.saasbackendstarter.repository.PermissionRepository;
import com.urke.saasbackendstarter.exception.PermissionNotFoundException;
import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
 * REST controller for managing user roles and permissions.
 * Only accessible to administrators.
 *
 * Endpoints:
 * - List all roles
 * - Create new role with permissions
 */
@Tag(
    name = "Roles",
    description = "Endpoints for listing and creating user roles. Only available to administrators."
)
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    @Operation(
        summary = "Get all roles",
        description = "Retrieve a list of all user roles in the system. Only accessible to users with the ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Roles listed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges")
        }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }

    @Operation(
        summary = "Create a new role",
        description = "Create a new user role. Only accessible to users with the ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @RequestBody(
            description = "Role data (name is required)",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges"),
            @ApiResponse(responseCode = "409", description = "Role already exists")
        }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> createRole(
            @Valid @org.springframework.web.bind.annotation.RequestBody RoleDTO roleDTO) {
        try {
            Set<Permission> permissions = new HashSet<>();
            if (roleDTO.getPermissions() != null && !roleDTO.getPermissions().isEmpty()) {
                permissions = roleDTO.getPermissions().stream()
                    .map(dto -> permissionRepository.findById(dto.getId())
                        .orElseThrow(() -> new PermissionNotFoundException("Permission not found: " + dto.getId())))
                    .collect(Collectors.toSet());
            }
            Role role = Role.builder()
                    .name(roleDTO.getName())
                    .permissions(permissions)
                    .build();
            Role saved = roleService.save(role);
            return ResponseEntity.ok(RoleMapper.toDTO(saved));
        } catch (Exception ex) {
            log.error("Error creating role: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}