package com.urke.saasbackendstarter.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(String action, String entityType, Long entityId, String message, String actorEmail);
    Page<AuditLog> findPagedFiltered(String action, String entityType, Pageable pageable);
}
