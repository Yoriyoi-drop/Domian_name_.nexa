package com.myproject.nexa.utils;

import com.myproject.nexa.config.I18nConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
public class LocaleUtil {

    /**
     * Detect the best matching locale from the request
     * @param request HTTP request
     * @return Best matching locale
     */
    public static Locale detectLocale(HttpServletRequest request) {
        // 1. Check for locale parameter in URL (e.g., ?lang=en)
        String langParam = request.getParameter("lang");
        if (langParam != null && !langParam.trim().isEmpty()) {
            Locale locale = parseLocale(langParam);
            if (I18nConfig.SUPPORTED_LOCALES.contains(locale)) {
                log.debug("Using locale from URL parameter: {}", locale);
                return locale;
            }
        }

        // 2. Check for locale in session
        Object sessionLocale = request.getSession().getAttribute("user.locale");
        if (sessionLocale instanceof Locale && I18nConfig.SUPPORTED_LOCALES.contains(sessionLocale)) {
            log.debug("Using locale from session: {}", sessionLocale);
            return (Locale) sessionLocale;
        }

        // 3. Check Accept-Language header
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null) {
            Locale detectedLocale = parseAcceptLanguageHeader(acceptLanguage);
            if (I18nConfig.SUPPORTED_LOCALES.contains(detectedLocale)) {
                log.debug("Using locale from Accept-Language header: {}", detectedLocale);
                return detectedLocale;
            }
        }

        // 4. Default to English
        log.debug("Using default locale: {}", Locale.ENGLISH);
        return Locale.ENGLISH;
    }

    /**
     * Parse a locale string (e.g., "en", "en_US", "id")
     * @param localeStr Locale string
     * @return Parsed locale
     */
    public static Locale parseLocale(String localeStr) {
        if (localeStr == null || localeStr.trim().isEmpty()) {
            return Locale.ENGLISH; // default
        }

        String[] parts = localeStr.trim().split("[-_]");
        switch (parts.length) {
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            case 3:
                return new Locale(parts[0], parts[1], parts[2]);
            default:
                return new Locale(parts[0]); // fallback to language only
        }
    }

    /**
     * Parse Accept-Language header to detect preferred locale
     * @param acceptLanguage Accept-Language header value
     * @return Detected locale
     */
    private static Locale parseAcceptLanguageHeader(String acceptLanguage) {
        // Simple parsing - in a production system, you might want more sophisticated parsing
        // that respects quality values (q-values) in the header
        String[] languages = acceptLanguage.split(",");
        for (String lang : languages) {
            // Extract just the language part, ignoring quality values
            String cleanLang = lang.trim().split(";")[0];
            Locale locale = parseLocale(cleanLang);
            if (I18nConfig.SUPPORTED_LOCALES.contains(locale)) {
                return locale;
            }
        }
        return Locale.ENGLISH; // default
    }

    /**
     * Validate if a locale is supported
     * @param locale Locale to validate
     * @return True if supported, false otherwise
     */
    public static boolean isSupportedLocale(Locale locale) {
        return I18nConfig.SUPPORTED_LOCALES.contains(locale);
    }
}