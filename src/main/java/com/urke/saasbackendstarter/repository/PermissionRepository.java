package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for system permissions.
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNameAndOrganizationId(String name, Long organizationId);
    boolean existsByNameAndOrganizationId(String name, Long organizationId);
    List<Permission> findAllByOrganizationId(Long organizationId);
}
