package com.myproject.nexa.security;

import com.myproject.nexa.utils.InputSanitizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filter to sanitize incoming requests and prevent common injection attacks
 */
@Component
@RequiredArgsConstructor
public class RequestSanitizationFilter extends OncePerRequestFilter {

    private final InputSanitizer inputSanitizer;

    // Pattern to detect potential SQL injection attempts
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union\\s+select|drop\\s+table|exec\\(|execute\\s+|insert\\s+into|update\\s+set|delete\\s+from|create\\s+table|alter\\s+table|declare\\s+|exec\\s+sp_|waitfor\\s+delay|benchmark\\(|extractvalue\\(|updatexml\\(|sleep\\(|pg_sleep\\(|xp_cmdshell|sp_|procedure|function)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );

    // Pattern to detect potential XSS attempts
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)<script[^>]*>.*?</script>|<img[^>]*src[\\s]*=[\\s]*['\"]javascript:|<iframe[^>]*>.*?</iframe>|javascript:|onload|onerror|onmouseover|onfocus|onblur|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseup|onreset|onresize|onscroll|onselect|onsubmit|onunload",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request instanceof SanitizedRequestWrapper) {
            // Skip if already wrapped
            filterChain.doFilter(request, response);
            return;
        }

        SanitizedRequestWrapper sanitizedRequest = new SanitizedRequestWrapper(request, inputSanitizer);
        filterChain.doFilter(sanitizedRequest, response);
    }

    /**
     * Inner class to wrap the request and sanitize parameters
     */
    private static class SanitizedRequestWrapper extends HttpServletRequestWrapper {
        private final InputSanitizer inputSanitizer;

        public SanitizedRequestWrapper(HttpServletRequest request, InputSanitizer inputSanitizer) {
            super(request);
            this.inputSanitizer = inputSanitizer;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }

            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitizeInput(values[i]);
            }
            return sanitizedValues;
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return sanitizeInput(value);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameterMap = super.getParameterMap();
            Map<String, String[]> sanitizedMap = new HashMap<>();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String[] values = entry.getValue();
                String[] sanitizedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    sanitizedValues[i] = sanitizeInput(values[i]);
                }
                sanitizedMap.put(entry.getKey(), sanitizedValues);
            }
            return sanitizedMap;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return sanitizeInput(value);
        }

        /**
         * Sanitize input by removing potential malicious content
         */
        private String sanitizeInput(String input) {
            if (input == null || input.trim().isEmpty()) {
                return input;
            }

            // Check for SQL injection patterns
            if (SQL_INJECTION_PATTERN.matcher(input).find()) {
                // Log the attempt and return null to block the input
                System.err.println("SQL injection attempt detected: " + input);
                return null;
            }

            // Check for XSS patterns
            if (XSS_PATTERN.matcher(input).find()) {
                // Log the attempt and return null to block the input
                System.err.println("XSS attempt detected: " + input);
                return null;
            }

            // Apply sanitization based on context
            // For now, we'll use the plain text sanitizer which removes dangerous characters
            return inputSanitizer.sanitizePlainText(input);
        }
    }
}