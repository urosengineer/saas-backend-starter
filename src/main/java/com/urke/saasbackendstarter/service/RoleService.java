package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Role;
import java.util.Optional;
import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService {
    Optional<Role> findByName(String name);
    List<Role> findAll();
    Role save(Role role);
}