package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.UserFile;
import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * JPA repository for user files.
 */
public interface UserFileRepository extends JpaRepository<UserFile, Long> {
    List<UserFile> findByUser(User user);
    List<UserFile> findByUserAndOrganization(User user, Organization organization);
}