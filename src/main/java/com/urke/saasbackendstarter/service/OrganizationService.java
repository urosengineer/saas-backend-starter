package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    Organization create(OrganizationCreateRequest request);
    List<Organization> findAll();
    Optional<Organization> findById(Long id);
    Optional<Organization> findByName(String name);
    Optional<Organization> findBySlug(String slug);
    void deleteOrganization(Long id);

    Page<Organization> findAll(Pageable pageable);
    Page<Organization> findAllByNameFilter(String name, Pageable pageable);
}