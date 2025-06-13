package com.urke.saasbackendstarter.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuration for internationalization (i18n) support.
 * <p>
 * Enables message localization based on the user's locale (from Accept-Language header).
 * Defaults to English if no locale is provided.
 */
@Configuration
public class I18nConfig {

    /**
     * Configures the message source to load localized messages from 'messages.properties' files.
     *
     * @return the configured MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false); // Always use the configured default, not system locale
        return messageSource;
    }

    /**
     * Configures the locale resolver to use the Accept-Language header,
     * defaulting to English if none is specified.
     *
     * @return the configured LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}