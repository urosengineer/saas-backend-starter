package com.urke.saasbackendstarter.audit;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public void log(String action, String entityType, Long entityId, String message, String actorEmail) {
        if (action == null || entityType == null || actorEmail == null) {
            throw new IllegalArgumentException("Action, entityType, and actorEmail must not be null.");
        }
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .actorEmail(actorEmail)
                .organization(currentUserProvider.getCurrentOrganization())
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLog> findPagedFiltered(String action, String entityType, Pageable pageable) {
        Organization org = currentUserProvider.getCurrentOrganization();

        if (action != null && !action.isBlank()) {
            return auditLogRepository.findByOrganizationAndActionContainingIgnoreCase(org, action, pageable);
        } else if (entityType != null && !entityType.isBlank()) {
            return auditLogRepository.findByOrganizationAndEntityTypeContainingIgnoreCase(org, entityType, pageable);
        } else {
            return auditLogRepository.findByOrganization(org, pageable);
        }
    }
}
