package com.urke.saasbackendstarter.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a system permission, scoped per organization (tenant).
 */
@Entity
@Table(name = "permissions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "organization_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name; // e.g. USER_UPDATE_SELF, USER_VIEW_ALL, ORG_MANAGE

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}