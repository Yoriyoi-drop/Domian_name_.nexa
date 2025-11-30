package com.myproject.nexa.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class I18nService {

    private final MessageSource messageSource;

    /**
     * Get a message in the current locale
     * @param code The message key
     * @return The localized message
     */
    public String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        log.debug("Getting message for code: {} in locale: {}", code, locale);
        return messageSource.getMessage(code, null, code, locale);
    }

    /**
     * Get a message in the current locale with arguments
     * @param code The message key
     * @param args Arguments for message formatting
     * @return The localized message
     */
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        log.debug("Getting message for code: {} with args in locale: {}", code, locale);
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * Get a message in a specific locale
     * @param code The message key
     * @param locale The target locale
     * @return The localized message
     */
    public String getMessage(String code, Locale locale) {
        log.debug("Getting message for code: {} in locale: {}", code, locale);
        return messageSource.getMessage(code, null, code, locale);
    }

    /**
     * Get a message in a specific locale with arguments
     * @param code The message key
     * @param args Arguments for message formatting
     * @param locale The target locale
     * @return The localized message
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        log.debug("Getting message for code: {} with args in locale: {}", code, locale);
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * Get the current locale
     * @return Current locale
     */
    public Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }
}