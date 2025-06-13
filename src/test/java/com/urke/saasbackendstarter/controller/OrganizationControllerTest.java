package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import com.urke.saasbackendstarter.dto.organization.OrganizationSummary;
import com.urke.saasbackendstarter.mapper.OrganizationMapper;
import com.urke.saasbackendstarter.service.OrganizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationController.class)
@Import(com.urke.saasbackendstarter.config.SecurityConfig.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @MockBean
    private OrganizationMapper organizationMapper;

    @MockBean
    private MessageSource messageSource;

    // DODAJTE OVO:
    @MockBean
    private com.urke.saasbackendstarter.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user@demo.com", roles = {"USER"})
    @DisplayName("POST /api/v1/organizations - create organization - success")
    void createOrganization_ShouldReturnSummary() throws Exception {
        OrganizationCreateRequest req = OrganizationCreateRequest.builder()
                .name("Test Org").build();
        Organization org = Organization.builder()
                .id(1L).name("Test Org").slug("test-org").build();
        OrganizationSummary summary = OrganizationSummary.builder()
                .id(1L).name("Test Org").slug("test-org").build();

        when(organizationService.create(any(OrganizationCreateRequest.class))).thenReturn(org);
        when(organizationMapper.toSummary(org)).thenReturn(summary);

        String json = """
                {"name": "Test Org"}
                """;

        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Org"))
                .andExpect(jsonPath("$.slug").value("test-org"));
    }

    @Test
    @WithMockUser(username = "admin@demo.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/organizations - get all paged - no filter")
    void getAllPaged_NoFilter() throws Exception {
        Organization org1 = Organization.builder().id(1L).name("Org1").slug("org1").build();
        Organization org2 = Organization.builder().id(2L).name("Org2").slug("org2").build();

        OrganizationSummary sum1 = OrganizationSummary.builder().id(1L).name("Org1").slug("org1").build();
        OrganizationSummary sum2 = OrganizationSummary.builder().id(2L).name("Org2").slug("org2").build();

        Page<Organization> orgPage = new PageImpl<>(List.of(org1, org2), PageRequest.of(0, 10), 2);
        Page<OrganizationSummary> sumPage = new PageImpl<>(List.of(sum1, sum2), PageRequest.of(0, 10), 2);

        when(organizationService.findAll(any(Pageable.class))).thenReturn(orgPage);
        when(organizationMapper.toSummary(org1)).thenReturn(sum1);
        when(organizationMapper.toSummary(org2)).thenReturn(sum2);

        mockMvc.perform(get("/api/v1/organizations?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "admin@demo.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/organizations - get all paged - with name filter")
    void getAllPaged_WithNameFilter() throws Exception {
        Organization org1 = Organization.builder().id(1L).name("Org1").slug("org1").build();
        OrganizationSummary sum1 = OrganizationSummary.builder().id(1L).name("Org1").slug("org1").build();

        Page<Organization> orgPage = new PageImpl<>(List.of(org1), PageRequest.of(0, 10), 1);

        when(organizationService.findAllByNameFilter(eq("Org1"), any(Pageable.class))).thenReturn(orgPage);
        when(organizationMapper.toSummary(org1)).thenReturn(sum1);

        mockMvc.perform(get("/api/v1/organizations?name=Org1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Org1"));
    }

    @Test
    @WithMockUser(username = "admin@demo.com", roles = {"ADMIN"})
    @DisplayName("DELETE /api/v1/organizations/{id} - admin can delete")
    void deleteOrganization_Admin_Success() throws Exception {
        doNothing().when(organizationService).deleteOrganization(eq(1L));
        mockMvc.perform(delete("/api/v1/organizations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@demo.com", roles = {"USER"})
    @DisplayName("DELETE /api/v1/organizations/{id} - non-admin forbidden")
    void deleteOrganization_NonAdmin_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/organizations/1"))
                .andExpect(status().isForbidden());
    }
}