package com.urke.saasbackendstarter.dto.user;

import lombok.*;

import java.util.Set;

/**
 * Full user details (for profile/detail screens).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {
    private Long id;
    private String email;
    private String fullName;
    private Set<String> roles;
    private String organizationName;
}