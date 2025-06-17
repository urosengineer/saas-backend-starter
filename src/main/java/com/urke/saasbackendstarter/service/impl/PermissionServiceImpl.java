package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Permission;
import com.urke.saasbackendstarter.repository.PermissionRepository;
import com.urke.saasbackendstarter.service.PermissionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing permissions.
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<Permission> findAllByOrganizationId(Long organizationId) {
        return permissionRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    public Optional<Permission> findByNameAndOrganizationId(String name, Long organizationId) {
        return permissionRepository.findByNameAndOrganizationId(name, organizationId);
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
}