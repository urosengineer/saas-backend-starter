package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.PasswordResetToken;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.exception.PasswordResetTokenExpiredException;
import com.urke.saasbackendstarter.exception.PasswordResetTokenInvalidException;
import com.urke.saasbackendstarter.exception.UserNotFoundException;
import com.urke.saasbackendstarter.repository.PasswordResetTokenRepository;
import com.urke.saasbackendstarter.repository.UserRepository;
import com.urke.saasbackendstarter.service.EmailService;
import com.urke.saasbackendstarter.service.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageSource messageSource;

    private static final int EXPIRES_MINUTES = 15;

    @Value("${frontend.reset.url:https://frontend-app/reset-password?token=}")
    private String resetUrlPrefix;

    @Override
    @Transactional
    public void createResetToken(String email) {
        Locale locale = LocaleContextHolder.getLocale();
        User user = userRepository.findByEmailAndDeletedFalse(email)
            .orElseThrow(() -> new UserNotFoundException(
                messageSource.getMessage("user.notfound", null, locale)
            ));
        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRES_MINUTES))
                .user(user)
                .build();
        tokenRepository.save(resetToken);

        String resetUrl = resetUrlPrefix + token;

        String subject = messageSource.getMessage("reset.password.subject", null, locale);
        String body = messageSource.getMessage(
                "reset.password.body",
                new Object[]{resetUrl, EXPIRES_MINUTES},
                locale
        );

        emailService.sendEmail(
                user.getEmail(),
                subject,
                body
        );
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Locale locale = LocaleContextHolder.getLocale();
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetTokenInvalidException(
                    messageSource.getMessage("password.reset.invalid", null, locale)
                ));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenExpiredException(
                messageSource.getMessage("password.reset.expired", null, locale)
            );
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.deleteById(resetToken.getId());

        String subject = messageSource.getMessage("reset.password.success.subject", null, locale);
        String body = messageSource.getMessage(
                "reset.password.success.body",
                new Object[]{user.getFullName()},
                locale
        );
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
