package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import com.urke.saasbackendstarter.dto.organization.OrganizationSummary;
import com.urke.saasbackendstarter.mapper.OrganizationMapper;
import com.urke.saasbackendstarter.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * REST controller for organization management: create, list, and soft-delete organizations.
 */
@Tag(
    name = "Organizations",
    description = "Endpoints for organization management: create, list, and soft delete organizations."
)
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;
    private final MessageSource messageSource;

    /**
     * Create and register a new organization entity.
     */
    @Operation(
        summary = "Create a new organization",
        description = "Create and register a new organization entity. Only accessible by authenticated users.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Organization created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PostMapping
    public ResponseEntity<OrganizationSummary> createOrg(
            @Valid @RequestBody OrganizationCreateRequest request,
            Locale locale) {
        Organization org = organizationService.create(request);
        OrganizationSummary summary = organizationMapper.toSummary(org);
        return ResponseEntity.ok().body(summary);
    }

    /**
     * Retrieve a paged list of all organizations, optionally filtered by name.
     */
    @Operation(
        summary = "Get paged organizations",
        description = "Retrieve a paged list of all organizations, optionally filtered by name.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "10"),
            @Parameter(name = "name", description = "Optional name filter (case-insensitive)")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "List of organizations returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @GetMapping
    public ResponseEntity<Page<OrganizationSummary>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Organization> orgPage;
        if (name != null && !name.isBlank()) {
            orgPage = organizationService.findAllByNameFilter(name, pageable);
        } else {
            orgPage = organizationService.findAll(pageable);
        }
        Page<OrganizationSummary> summaries = orgPage.map(organizationMapper::toSummary);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Soft delete an organization by its ID. Only accessible by admins.
     */
    @Operation(
        summary = "Delete organization (soft delete)",
        description = "Soft delete an organization by its ID. Only accessible by admins.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "id", description = "ID of the organization to delete", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "Organization deleted successfully (no content)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
        }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id, Locale locale) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}