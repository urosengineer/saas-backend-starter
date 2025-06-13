package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndDeletedFalse(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmailWithOrganization(@Param("email") String email);
    
    List<User> findAllByOrganizationAndDeletedFalse(Organization organization);
    List<User> findAllByDeletedFalse();
    Optional<User> findByIdAndDeletedFalse(Long id);


    // Paginated and filtered
    Page<User> findAllByDeletedFalse(Pageable pageable);
    Page<User> findByEmailContainingIgnoreCaseAndDeletedFalse(String email, Pageable pageable);
}