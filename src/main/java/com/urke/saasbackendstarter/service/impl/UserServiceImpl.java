package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.dto.user.UserCreateRequest;
import com.urke.saasbackendstarter.dto.user.UserUpdateRequest;
import com.urke.saasbackendstarter.events.UserEvent;
import com.urke.saasbackendstarter.exception.UserAlreadyExistsException;
import com.urke.saasbackendstarter.exception.UserNotFoundException;
import com.urke.saasbackendstarter.repository.UserRepository;
import com.urke.saasbackendstarter.service.UserService;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import com.urke.saasbackendstarter.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public User register(UserCreateRequest request) {
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new UserAlreadyExistsException(
                messageSource.getMessage("user.exists", null, LocaleContextHolder.getLocale())
            );
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException(
                    messageSource.getMessage("role.notfound", null, LocaleContextHolder.getLocale())
                ));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of(userRole))
                .organization(organization)
                .deleted(false)
                .build();

        User saved = userRepository.save(user);
        eventPublisher.publishEvent(new UserEvent(this, UserEvent.Type.REGISTERED, saved));
        return saved;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    @Override
    public Optional<User> findByEmailWithOrganization(String email) {
        return userRepository.findByEmailWithOrganization(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByDeletedFalse();
    }

    @Override
    public List<User> findAllByOrganization(Organization organization) {
        return userRepository.findAllByOrganizationAndDeletedFalse(organization);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAllByDeletedFalse(pageable);
    }

    @Override
    public Page<User> findAllByEmailFilter(String email, Pageable pageable) {
        return userRepository.findByEmailContainingIgnoreCaseAndDeletedFalse(email, pageable);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException(
                    messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())
                ));

        user.setFullName(request.getFullName());
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        User updated = userRepository.save(user);
        eventPublisher.publishEvent(new UserEvent(this, UserEvent.Type.UPDATED, updated));
        return updated;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException(
                    messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())
                ));
        user.setDeleted(true);
        userRepository.save(user);
        eventPublisher.publishEvent(new UserEvent(this, UserEvent.Type.DELETED, user));
    }
}