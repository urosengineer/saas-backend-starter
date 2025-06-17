package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for user roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameAndOrganizationId(String name, Long organizationId);
    boolean existsByNameAndOrganizationId(String name, Long organizationId);
    List<Role> findAllByOrganizationId(Long organizationId);
}
