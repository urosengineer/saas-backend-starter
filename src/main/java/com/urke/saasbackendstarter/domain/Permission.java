package com.urke.saasbackendstarter.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a system permission.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String name; // e.g. USER_UPDATE_SELF, USER_VIEW_ALL, ORG_MANAGE
}