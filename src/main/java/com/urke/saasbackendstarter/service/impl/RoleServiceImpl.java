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
 * Service implementation for managing roles.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MessageSource messageSource;

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role save(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RoleAlreadyExistsException(
                messageSource.getMessage("role.exists", null, LocaleContextHolder.getLocale())
            );
        }
        return roleRepository.save(role);
    }
}