package com.urke.saasbackendstarter.security;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import com.urke.saasbackendstarter.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Provides access to the currently authenticated user and organization context.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * Returns the currently authenticated user, or throws if not found.
     */
    public User getCurrentUser() {
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())
                ));
    }

    /**
     * Returns the organization for the currently authenticated user.
     */
    public Organization getCurrentOrganization() {
        return getCurrentUser().getOrganization();
    }
}