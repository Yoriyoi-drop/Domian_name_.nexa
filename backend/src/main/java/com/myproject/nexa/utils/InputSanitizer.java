package com.myproject.nexa.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * Utility class for input sanitization using JSoup
 */
@Component
public class InputSanitizer {

    /**
     * Basic HTML sanitization policy that allows common formatting elements
     */
    private static final Safelist BASIC_HTML_LIST = Safelist.relaxed()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6", "p", "br", "ul", "ol", "li");

    /**
     * Policy that allows only basic text formatting
     */
    private static final Safelist TEXT_FORMATTING_LIST = new Safelist()
            .addTags("strong", "em", "u", "b", "i");

    /**
     * Strict policy that removes all HTML tags
     */
    private static final Safelist STRIP_HTML_LIST = new Safelist();

    /**
     * Sanitize HTML content with basic formatting allowed
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public String sanitizeHtml(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        return Jsoup.clean(input, BASIC_HTML_LIST);
    }

    /**
     * Sanitize input but only allow text formatting (strong, em, u)
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public String sanitizeTextFormatting(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        return Jsoup.clean(input, TEXT_FORMATTING_LIST);
    }

    /**
     * Strip all HTML tags from input
     * @param input The input string to sanitize
     * @return Clean string without HTML tags
     */
    public String stripHtml(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        return Jsoup.clean(input, STRIP_HTML_LIST);
    }

    /**
     * Sanitize URL input
     * @param input The URL string to sanitize
     * @return Sanitized URL
     */
    public String sanitizeUrl(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        // Basic URL validation and sanitization
        return input.trim().replaceAll("[<>\\\"\\'\\(\\)]", "");
    }

    /**
     * Sanitize plain text input
     * @param input The text string to sanitize
     * @return Sanitized text
     */
    public String sanitizePlainText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'();&]", "");
    }

    /**
     * Sanitize filename to prevent path traversal
     * @param filename The filename to sanitize
     * @return Sanitized filename
     */
    public String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return filename;
        }
        // Remove path traversal attempts and dangerous characters
        return filename.replaceAll("\\.\\./", "") // Prevent directory traversal
                .replaceAll("\\.\\.\\\\", "") // Prevent Windows-style traversal
                .replaceAll("[<>:\"/\\\\|?*]", ""); // Remove invalid filename characters
    }
}