package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Permission;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing permissions.
 */
public interface PermissionService {
    List<Permission> findAllByOrganizationId(Long organizationId);
    Optional<Permission> findByNameAndOrganizationId(String name, Long organizationId);
    Permission save(Permission permission);
}