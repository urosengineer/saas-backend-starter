package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByNameAndDeletedFalse(String name);
    Optional<Organization> findBySlugAndDeletedFalse(String slug);
    boolean existsByNameAndDeletedFalse(String name);
    boolean existsBySlugAndDeletedFalse(String slug);
    List<Organization> findAllByDeletedFalse();
    Optional<Organization> findByIdAndDeletedFalse(Long id);

    // Paginated and filtered
    Page<Organization> findAllByDeletedFalse(Pageable pageable);
    Page<Organization> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);
}