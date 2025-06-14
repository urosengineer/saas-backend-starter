package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.mapper.UserMapper;
import com.urke.saasbackendstarter.service.UserExportService;
import com.urke.saasbackendstarter.service.UserService;
import com.urke.saasbackendstarter.security.CurrentUserProvider;
import com.urke.saasbackendstarter.security.JwtTokenProvider;
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

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(com.urke.saasbackendstarter.security.SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserExportService userExportService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private UserMapper userMapper;

    @Test
    @WithMockUser(username = "user1@demo.com", roles = {"USER"})
    @DisplayName("Non-admin user cannot delete a user from another organization – should return 403 Forbidden")
    void deleteUser_NonAdmin_CannotDeleteUserFromOtherOrg() throws Exception {
        // Organizations
        Organization org1 = Organization.builder().id(1L).name("Org1").build();
        Organization org2 = Organization.builder().id(2L).name("Org2").build();

        Role userRole = Role.builder().id(1L).name("USER").build();

        // Current user (from org1)
        User currentUser = User.builder()
                .id(11L)
                .email("user1@demo.com")
                .fullName("Test User 1")
                .roles(Set.of(userRole))
                .organization(org1)
                .deleted(false)
                .build();

        // Target user (from another organization)
        User targetUser = User.builder()
                .id(22L)
                .email("user2@demo.com")
                .fullName("Target User 2")
                .roles(Set.of(userRole))
                .organization(org2)
                .deleted(false)
                .build();

        when(userService.findByEmail(eq("user1@demo.com"))).thenReturn(Optional.of(currentUser));
        when(userService.findById(eq(22L))).thenReturn(Optional.of(targetUser));

        mockMvc.perform(delete("/api/v1/users/22"))
            .andExpect(status().isForbidden());
    }
}