package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.mapper.PermissionMapper;
import com.urke.saasbackendstarter.service.PermissionService;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
import com.urke.saasbackendstarter.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
@Import(SecurityConfig.class)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private PermissionMapper permissionMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private MessageSource messageSource;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return list of permissions for admin")
    void getAllPermissions_ReturnsList() throws Exception {
        // Arrange
        Permission p1 = Permission.builder().id(1L).name("USER_VIEW_ALL").build();
        Permission p2 = Permission.builder().id(2L).name("ORG_MANAGE").build();
        List<Permission> permissions = Arrays.asList(p1, p2);

        when(permissionService.findAll()).thenReturn(permissions);

        // Mock PermissionMapper
        when(permissionMapper.toDto(p1)).thenReturn(
                com.urke.saasbackendstarter.dto.PermissionDTO.builder().id(1L).name("USER_VIEW_ALL").build()
        );
        when(permissionMapper.toDto(p2)).thenReturn(
                com.urke.saasbackendstarter.dto.PermissionDTO.builder().id(2L).name("ORG_MANAGE").build()
        );

        // Act & Assert
        mockMvc.perform(get("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions", hasSize(2)))
                .andExpect(jsonPath("$.permissions[0].id", is(1)))
                .andExpect(jsonPath("$.permissions[0].name", is("USER_VIEW_ALL")))
                .andExpect(jsonPath("$.permissions[1].id", is(2)))
                .andExpect(jsonPath("$.permissions[1].name", is("ORG_MANAGE")))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return forbidden for non-admin user")
    void getAllPermissions_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}