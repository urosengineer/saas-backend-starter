package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import com.urke.saasbackendstarter.events.OrganizationEvent;
import com.urke.saasbackendstarter.exception.OrganizationAlreadyExistsException;
import com.urke.saasbackendstarter.exception.OrganizationNotFoundException;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import com.urke.saasbackendstarter.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing organizations.
 */
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public Organization create(OrganizationCreateRequest request) {
        if (organizationRepository.existsByNameAndDeletedFalse(request.getName())) {
            throw new OrganizationAlreadyExistsException(
                messageSource.getMessage("org.exists", null, LocaleContextHolder.getLocale())
            );
        }
        String slug = toSlug(request.getName());
        if (organizationRepository.existsBySlugAndDeletedFalse(slug)) {
            throw new OrganizationAlreadyExistsException(
                messageSource.getMessage("org.exists", null, LocaleContextHolder.getLocale())
            );
        }
        Organization organization = Organization.builder()
                .name(request.getName())
                .slug(slug)
                .deleted(false)
                .build();
        Organization saved = organizationRepository.save(organization);
        eventPublisher.publishEvent(new OrganizationEvent(this, OrganizationEvent.Type.CREATED, saved));
        return saved;
    }

    @Override
    public List<Organization> findAll() {
        return organizationRepository.findAllByDeletedFalse();
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return organizationRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Optional<Organization> findByName(String name) {
        return organizationRepository.findByNameAndDeletedFalse(name);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return organizationRepository.findBySlugAndDeletedFalse(slug);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        Organization organization = organizationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new OrganizationNotFoundException(
                    messageSource.getMessage("org.notfound", null, LocaleContextHolder.getLocale())
                ));
        organization.setDeleted(true);
        organizationRepository.save(organization);
        eventPublisher.publishEvent(new OrganizationEvent(this, OrganizationEvent.Type.DELETED, organization));
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAllByDeletedFalse(pageable);
    }

    @Override
    public Page<Organization> findAllByNameFilter(String name, Pageable pageable) {
        return organizationRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name, pageable);
    }

    /**
     * Generates a URL-friendly slug from a given string.
     * Removes diacritics and converts spaces/punctuation to hyphens.
     */
    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}