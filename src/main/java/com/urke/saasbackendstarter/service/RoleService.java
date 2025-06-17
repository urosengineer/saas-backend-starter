package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Role;

import java.util.Optional;
import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService {
    Optional<Role> findByNameAndOrganizationId(String name, Long organizationId);
    List<Role> findAllByOrganizationId(Long organizationId);
    Role save(Role role);
}