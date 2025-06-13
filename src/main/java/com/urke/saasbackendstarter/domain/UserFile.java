package com.urke.saasbackendstarter.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing a file uploaded by a user.
 */
@Entity
@Table(name = "user_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}