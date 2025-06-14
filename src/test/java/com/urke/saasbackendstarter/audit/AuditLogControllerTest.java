package com.urke.saasbackendstarter.audit;

import com.urke.saasbackendstarter.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AuditLogController}.
 * Verifies that audit log endpoints are accessible and secured according to roles.
 */
@WebMvcTest(AuditLogController.class)
@Import(com.urke.saasbackendstarter.security.SecurityConfig.class)
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "admin@demo.com", roles = {"ADMIN"})
    @DisplayName("Admin can retrieve paginated audit logs")
    void getAllPaged_AsAdmin_ReturnsPagedLogs() throws Exception {
        AuditLog log1 = AuditLog.builder()
                .id(1L)
                .action("LOGIN")
                .entityType("USER")
                .entityId(123L)
                .message("User logged in")
                .timestamp(LocalDateTime.now())
                .actorEmail("admin@demo.com")
                .build();
        AuditLog log2 = AuditLog.builder()
                .id(2L)
                .action("UPDATE")
                .entityType("ORG")
                .entityId(12L)
                .message("Org updated")
                .timestamp(LocalDateTime.now())
                .actorEmail("admin@demo.com")
                .build();
        Page<AuditLog> page = new PageImpl<>(List.of(log1, log2), PageRequest.of(0, 10), 2);

        Mockito.when(auditLogService.findPagedFiltered(
                any(), any(), any(Pageable.class))
        ).thenReturn(page);

        mockMvc.perform(get("/api/v1/audit-logs?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].action").value("LOGIN"))
                .andExpect(jsonPath("$.content[1].entityType").value("ORG"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "user@demo.com", roles = {"USER"})
    @DisplayName("Non-admin user cannot access audit logs (should return 403 Forbidden)")
    void getAllPaged_NotAdmin_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}