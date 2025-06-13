package com.urke.saasbackendstarter.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.urke.saasbackendstarter.domain.Organization;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column
    private Long entityId;

    @Column(length = 256)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 80)
    private String actorEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}