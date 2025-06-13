package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Permission;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing permissions.
 */
public interface PermissionService {
    List<Permission> findAll();
    Optional<Permission> findById(Long id);
    Optional<Permission> findByName(String name);
    Permission save(Permission permission);
}