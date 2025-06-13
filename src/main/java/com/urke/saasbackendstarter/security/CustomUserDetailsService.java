package com.urke.saasbackendstarter.security;

import com.urke.saasbackendstarter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService for loading users by email.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndDeletedFalse(email)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException(
                messageSource.getMessage("user.notfound", null, LocaleContextHolder.getLocale())
            ));
    }
}