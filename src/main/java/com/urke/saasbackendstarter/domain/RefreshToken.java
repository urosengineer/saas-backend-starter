package com.urke.saasbackendstarter.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Entity representing a refresh token used for JWT authentication.
 *
 * Each token is associated with a user and has an expiry date.
 * Used to issue new access tokens without re-authentication.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}