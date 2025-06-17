package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.service.PermissionService;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
import com.urke.saasbackendstarter.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private com.urke.saasbackendstarter.repository.OrganizationRepository organizationRepository;

    @MockBean
    private com.urke.saasbackendstarter.mapper.PermissionMapper permissionMapper;

    @MockBean
    private MessageSource messageSource;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return list of permissions for admin and organization")
    void getAllPermissionsForOrganization_ReturnsList() throws Exception {
        Long orgId = 1L;
        Permission p1 = Permission.builder().id(1L).name("USER_VIEW_ALL")
                .organization(com.urke.saasbackendstarter.domain.Organization.builder()
                        .id(orgId).name("Demo Org").slug("demo-org").build()).build();
        Permission p2 = Permission.builder().id(2L).name("ORG_MANAGE")
                .organization(com.urke.saasbackendstarter.domain.Organization.builder()
                        .id(orgId).name("Demo Org").slug("demo-org").build()).build();
        List<Permission> permissions = Arrays.asList(p1, p2);

        when(permissionService.findAllByOrganizationId(orgId)).thenReturn(permissions);

        mockMvc.perform(get("/api/v1/permissions/organization/{organizationId}", orgId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("USER_VIEW_ALL")))
                .andExpect(jsonPath("$[0].organization.id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("ORG_MANAGE")))
                .andExpect(jsonPath("$[1].organization.slug", is("demo-org")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return forbidden for non-admin user")
    void getAllPermissionsForOrganization_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/permissions/organization/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
