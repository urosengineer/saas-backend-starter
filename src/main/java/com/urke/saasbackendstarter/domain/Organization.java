package com.urke.saasbackendstarter.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an organization (tenant/company) in the system.
 * 
 * Each organization has a unique name and slug, and may be soft-deleted.
 */
@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false)
    private boolean deleted = false;
}