package com.urke.saasbackendstarter.config;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.repository.PermissionRepository;
import com.urke.saasbackendstarter.repository.RoleRepository;
import com.urke.saasbackendstarter.repository.UserRepository;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@Profile("!test")
public class StartupDataConfig {

    @Bean
    public CommandLineRunner initRolesAndPermissions(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Permissions
            Permission pUserUpdateSelf = permissionRepository.findByName("USER_UPDATE_SELF")
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("USER_UPDATE_SELF").build()));
            Permission pUserViewAll = permissionRepository.findByName("USER_VIEW_ALL")
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("USER_VIEW_ALL").build()));
            Permission pUserDelete = permissionRepository.findByName("USER_DELETE")
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("USER_DELETE").build()));
            Permission pOrgManage = permissionRepository.findByName("ORG_MANAGE")
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name("ORG_MANAGE").build()));

            // Roles
            Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(
                    Role.builder().name("USER").permissions(Set.of(pUserUpdateSelf)).build()));
            if (userRole.getPermissions() == null || userRole.getPermissions().isEmpty()) {
                userRole.setPermissions(Set.of(pUserUpdateSelf));
                roleRepository.save(userRole);
            }

            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(
                    Role.builder().name("ADMIN").permissions(Set.of(pUserUpdateSelf, pUserViewAll, pUserDelete, pOrgManage)).build()));
            if (adminRole.getPermissions() == null || adminRole.getPermissions().isEmpty()) {
                adminRole.setPermissions(Set.of(pUserUpdateSelf, pUserViewAll, pUserDelete, pOrgManage));
                roleRepository.save(adminRole);
            }

            // === ORGANIZATION SEED ===
            Organization demoOrg = organizationRepository.findBySlugAndDeletedFalse("demo-org")
            	    .orElseGet(() -> organizationRepository.save(
            	        Organization.builder()
            	            .name("Demo Org")
            	            .slug("demo-org")
            	            .deleted(false)
            	            .build()
            	));

            // === ADMIN USER SEED ===
            if (userRepository.findByEmailAndDeletedFalse("admin@demo.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@demo.com")
                        .password(passwordEncoder.encode("admin12345"))
                        .fullName("Admin User")
                        .roles(Set.of(adminRole))
                        .organization(demoOrg)
                        .deleted(false)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}