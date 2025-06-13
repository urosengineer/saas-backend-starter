package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserExportServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserExportServiceImpl userExportService;

    private Organization org;
    private User user1, user2;
    private Role roleUser, roleAdmin;

    @BeforeEach
    void setUp() {
        org = Organization.builder().id(1L).name("Org").slug("org").deleted(false).build();

        roleUser = new Role();
        roleUser.setName("USER");
        roleAdmin = new Role();
        roleAdmin.setName("ADMIN");

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("first@example.com");
        user1.setFullName("First User");
        user1.setRoles(Set.of(roleUser));
        user1.setOrganization(org);

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("second@example.com");
        user2.setFullName("Second User");
        user2.setRoles(Set.of(roleUser, roleAdmin));
        user2.setOrganization(org);
    }

    @Test
    void findExportUsers_shouldReturnAll_whenNoEmail() {
        when(userRepository.findAllByOrganizationAndDeletedFalse(org)).thenReturn(List.of(user1, user2));
        List<User> users = userExportService.findExportUsers(org, "");
        assertThat(users).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void findExportUsers_shouldFilterByEmail() {
        when(userRepository.findAllByOrganizationAndDeletedFalse(org)).thenReturn(List.of(user1, user2));
        List<User> users = userExportService.findExportUsers(org, "second");
        assertThat(users).containsExactly(user2);
    }

    @Test
    void exportToExcel_shouldProduceExcelFile() {
        List<User> users = List.of(user1, user2);
        byte[] data = userExportService.exportToExcel(users);

        assertThat(data).isNotEmpty();

        assertThat(data[0]).isEqualTo((byte) 'P');
        assertThat(data[1]).isEqualTo((byte) 'K');
    }

    @Test
    void exportToPdf_shouldProducePdfFile() {
        List<User> users = List.of(user1, user2);
        byte[] data = userExportService.exportToPdf(users);

        assertThat(data).isNotEmpty();
        String signature = new String(Arrays.copyOf(data, 4));
        assertThat(signature).isEqualTo("%PDF");
    }
}