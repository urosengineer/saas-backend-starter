package com.urke.saasbackendstarter.dto.user;

import lombok.*;

import java.util.Set;

/**
 * Lightweight summary of user information (for lists).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummary {
    private Long id;
    private String email;
    private String fullName;
    private Set<String> roles;
}