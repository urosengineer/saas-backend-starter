package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.dto.user.UserSummary;
import com.urke.saasbackendstarter.dto.user.UserDetails;
import com.urke.saasbackendstarter.dto.user.UserCreateRequest;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between User entity and User DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts User entity to UserSummary DTO.
     *
     * @param user User entity
     * @return UserSummary DTO or null if input is null
     */
    public UserSummary toSummary(User user) {
        if (user == null) return null;
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(extractRoleNames(user))
                .build();
    }

    /**
     * Converts User entity to UserDetails DTO.
     *
     * @param user User entity
     * @return UserDetails DTO or null if input is null
     */
    public UserDetails toDetails(User user) {
        if (user == null) return null;
        return UserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(extractRoleNames(user))
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .build();
    }

    /**
     * Converts UserCreateRequest DTO to User entity (without password encoding or roles/organization).
     */
    public User toEntity(UserCreateRequest dto) {
        if (dto == null) return null;
        return User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(dto.getPassword())
                .build();
    }

    private Set<String> extractRoleNames(User user) {
        if (user.getRoles() == null) return Set.of();
        return user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());
    }
}