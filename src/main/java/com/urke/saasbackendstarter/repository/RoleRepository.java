package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * JPA repository for user roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}