package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.dto.user.UserCreateRequest;
import com.urke.saasbackendstarter.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(UserCreateRequest request);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailWithOrganization(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    List<User> findAllByOrganization(Organization organization);
    User updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);

    Page<User> findAll(Pageable pageable);
    Page<User> findAllByEmailFilter(String email, Pageable pageable);
}