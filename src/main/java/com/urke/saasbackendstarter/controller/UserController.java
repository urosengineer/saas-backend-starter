package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.dto.user.UserSummary;
import com.urke.saasbackendstarter.dto.user.UserDetails;
import com.urke.saasbackendstarter.dto.user.UserCreateRequest;
import com.urke.saasbackendstarter.dto.user.UserUpdateRequest;
import com.urke.saasbackendstarter.mapper.UserMapper;
import com.urke.saasbackendstarter.exception.UserNotFoundException;
import com.urke.saasbackendstarter.security.CurrentUserProvider;
import com.urke.saasbackendstarter.service.UserExportService;
import com.urke.saasbackendstarter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@Tag(
    name = "Users",
    description = "Endpoints for user registration, management, and profile operations."
)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserExportService userExportService;
    private final CurrentUserProvider currentUserProvider;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    private String msg(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private String msg(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @Operation(
        summary = "Register new user",
        description = "Register a new user account. Open for everyone (no authentication required).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        	description = "User registration data",
        	required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PostMapping("/register")
    public ResponseEntity<UserSummary> register(
            @Valid @RequestBody UserCreateRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(userMapper.toSummary(user));
    }

    @Operation(
        summary = "Get all users (paged)",
        description = "Admins can view all users. Non-admins can view only users from their own organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based, default: 0)", example = "0"),
            @Parameter(name = "size", description = "Page size (default: 10)", example = "10"),
            @Parameter(name = "email", description = "Optional email filter")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Paged list of users returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW_ALL') or hasRole('ADMIN')")
    public ResponseEntity<Page<UserSummary>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String email,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));

        boolean isAdmin = currentUser.getRoles().stream()
                .map(r -> r.getName())
                .anyMatch(role -> role.equals("ADMIN"));

        Page<User> usersPage;
        if (isAdmin) {
            if (email != null && !email.isBlank()) {
                usersPage = userService.findAllByEmailFilter(email, pageable);
            } else {
                usersPage = userService.findAll(pageable);
            }
        } else {
            List<User> users = userService.findAllByOrganization(currentUser.getOrganization());
            usersPage = new PageImpl<>(users, pageable, users.size());
        }
        Page<UserSummary> result = usersPage.map(userMapper::toSummary);
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Update user",
        description = "Update user profile. Admins can update any user; regular users can update only their own profile in their own organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "id", description = "ID of the user to update", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration data",
        	required = true
        ),			
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE_SELF') or hasRole('ADMIN')")
    public ResponseEntity<UserSummary> updateUser(
    		@PathVariable Long id,
    	    @Valid @RequestBody UserUpdateRequest request,
    	    Principal principal) {
    	
    	System.out.println("[DEBUG] req: " + request);
        System.out.println("[DEBUG] fullName: " + request.getFullName());
        System.out.println("[DEBUG] class: " + request.getClass());

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));
        User targetUser = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));

        boolean isAdmin = currentUser.getRoles().stream()
                .map(r -> r.getName())
                .anyMatch(role -> role.equals("ADMIN"));

        if (!isAdmin) {
            if (!currentUser.getOrganization().getId().equals(targetUser.getOrganization().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg("org.notfound"));
            }
            if (!currentUser.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg("user.notfound"));
            }
        }

        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(userMapper.toSummary(updated));
    }

    @Operation(
        summary = "Get user by ID",
        description = "Get details of a user by ID. Admins can access any user; regular users only within their own organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "id", description = "User ID", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "User details returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW_ALL') or hasRole('ADMIN') or hasAuthority('USER_UPDATE_SELF')")
    public ResponseEntity<UserDetails> getById(
            @PathVariable Long id,
            Principal principal) {
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));

        boolean isAdmin = currentUser.getRoles().stream()
                .map(r -> r.getName())
                .anyMatch(role -> role.equals("ADMIN"));

        if (!currentUser.getId().equals(id)
                && !isAdmin
                && (user.getOrganization() != null && !user.getOrganization().equals(currentUser.getOrganization()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg("org.notfound"));
        }
        return ResponseEntity.ok(userMapper.toDetails(user));
    }

    @Operation(
        summary = "Delete user",
        description = "Delete a user by ID. Admins can delete any user; regular users can delete only themselves within their own organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "id", description = "User ID to delete", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            Principal principal) {

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));
        User targetUser = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(msg("user.notfound")));

        boolean isAdmin = currentUser.getRoles().stream()
                .map(r -> r.getName())
                .anyMatch(role -> role.equals("ADMIN"));

        if (!isAdmin) {
            if (!currentUser.getOrganization().getId().equals(targetUser.getOrganization().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg("org.notfound"));
            }
            if (!currentUser.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg("user.notfound"));
            }
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Export users as PDF or Excel",
        description = "Exports users of current organization as PDF (.pdf) or Excel (.xlsx). Only admins can export all, regular users export their organization.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "format", description = "Export format: 'pdf' or 'xlsx'", required = true, example = "pdf"),
            @Parameter(name = "email", description = "Optional filter by email")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "File exported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid format or filter")
        }
    )
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('USER_VIEW_ALL') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam String format,
            @RequestParam(required = false) String email,
            Principal principal) {

        Organization org = currentUserProvider.getCurrentOrganization();
        List<User> users = userExportService.findExportUsers(org, email);

        if ("xlsx".equalsIgnoreCase(format)) {
            byte[] file = userExportService.exportToExcel(users);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                    .body(file);
        } else if ("pdf".equalsIgnoreCase(format)) {
            byte[] file = userExportService.exportToPdf(users);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf")
                    .body(file);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg("validation.error"));
        }
    }
}