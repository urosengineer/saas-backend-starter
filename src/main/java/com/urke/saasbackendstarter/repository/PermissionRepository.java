package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * JPA repository for system permissions.
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}