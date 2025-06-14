package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.repository.PermissionRepository;
import com.urke.saasbackendstarter.service.RoleService;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
import com.urke.saasbackendstarter.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private PermissionRepository permissionRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return list of roles for admin")
    void getAllRoles_ReturnsList() throws Exception {
        Permission p1 = Permission.builder().id(1L).name("USER_VIEW_ALL").build();
        Role r1 = Role.builder().id(10L).name("ADMIN").permissions(Set.of(p1)).build();
        List<Role> roles = List.of(r1);

        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].name", is("ADMIN")))
                .andExpect(jsonPath("$[0].permissions", hasSize(1)))
                .andExpect(jsonPath("$[0].permissions[0].id", is(1)))
                .andExpect(jsonPath("$[0].permissions[0].name", is("USER_VIEW_ALL")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return forbidden for non-admin user")
    void getAllRoles_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create role for admin")
    void createRole_ReturnsCreatedRole() throws Exception {
        Role role = Role.builder().id(20L).name("MODERATOR").permissions(Collections.emptySet()).build();

        when(roleService.save(org.mockito.ArgumentMatchers.any(Role.class))).thenReturn(role);

        String requestBody = """
            {
                "name": "MODERATOR",
                "permissions": []
            }
        """;

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(20)))
                .andExpect(jsonPath("$.name", is("MODERATOR")))
                .andExpect(jsonPath("$.permissions", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should not allow non-admin to create role")
    void createRole_ForbiddenForNonAdmin() throws Exception {
        String requestBody = """
            {
                "name": "MODERATOR",
                "permissions": []
            }
        """;
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}