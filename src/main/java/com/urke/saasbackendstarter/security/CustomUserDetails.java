package com.urke.saasbackendstarter.security;

import com.urke.saasbackendstarter.domain.Role;
import com.urke.saasbackendstarter.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation for Spring Security.
 * Wraps the application's User entity.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            if (role.getPermissions() != null) {
                authorities.addAll(role.getPermissions().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getName()))
                        .collect(Collectors.toSet()));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}