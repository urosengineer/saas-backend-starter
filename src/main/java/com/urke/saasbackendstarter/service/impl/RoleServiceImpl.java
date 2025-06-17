package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.exception.RoleAlreadyExistsException;
import com.urke.saasbackendstarter.repository.RoleRepository;
import com.urke.saasbackendstarter.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

/**
 * Service implementation for managing roles (per organization).
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MessageSource messageSource;

    @Override
    public Optional<Role> findByNameAndOrganizationId(String name, Long organizationId) {
        return roleRepository.findByNameAndOrganizationId(name, organizationId);
    }

    @Override
    public List<Role> findAllByOrganizationId(Long organizationId) {
        return roleRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    public Role save(Role role) {
        if (roleRepository.existsByNameAndOrganizationId(role.getName(), role.getOrganization().getId())) {
            throw new RoleAlreadyExistsException(
                messageSource.getMessage("role.exists", null, LocaleContextHolder.getLocale())
            );
        }
        return roleRepository.save(role);
    }
}