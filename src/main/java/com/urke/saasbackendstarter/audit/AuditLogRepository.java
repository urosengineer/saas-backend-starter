package com.urke.saasbackendstarter.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.urke.saasbackendstarter.domain.Organization;

/**
 * Repository for accessing audit log records.
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Returns paged audit logs for a given organization.
     */
    Page<AuditLog> findByOrganization(Organization organization, Pageable pageable);

    /**
     * Returns paged audit logs filtered by action (case-insensitive, contains) and organization.
     */
    Page<AuditLog> findByOrganizationAndActionContainingIgnoreCase(Organization organization, String action, Pageable pageable);

    /**
     * Returns paged audit logs filtered by entityType (case-insensitive, contains) and organization.
     */
    Page<AuditLog> findByOrganizationAndEntityTypeContainingIgnoreCase(Organization organization, String entityType, Pageable pageable);
}