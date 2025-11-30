package com.myproject.nexa.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for HTTP cookie management
 */
@Component
@Slf4j
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    /**
     * Set HttpOnly, Secure cookie with SameSite attribute
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge); // in seconds
        cookie.setHttpOnly(httpOnly); // Prevent access from JavaScript
        cookie.setSecure(true); // Only sent over HTTPS
        cookie.setPath("/"); // Available to entire app

        response.addCookie(cookie);

        // Add SameSite attribute via header since Cookie class doesn't support it
        // This should be done in a filter or by modifying response headers directly
        if (name.equals("access_token") || name.equals("refresh_token")) {
            response.setHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                    name, value, maxAge));
        }
    }

    /**
     * Set access token as HttpOnly cookie
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token, int maxAge) {
        setCookie(response, ACCESS_TOKEN_COOKIE, token, maxAge, true);
        log.debug("Access token cookie set");
    }

    /**
     * Set refresh token as HttpOnly cookie
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token, int maxAge) {
        setCookie(response, REFRESH_TOKEN_COOKIE, token, maxAge, true);
        log.debug("Refresh token cookie set");
    }

    /**
     * Remove token cookies
     */
    public void removeTokenCookies(HttpServletResponse response) {
        setCookie(response, ACCESS_TOKEN_COOKIE, "", 0, true);
        setCookie(response, REFRESH_TOKEN_COOKIE, "", 0, true);
        log.debug("Token cookies removed");
    }

    /**
     * Get cookie by name from request
     * Note: This is for non-HttpOnly cookies. HttpOnly cookies can't be accessed client-side
     */
    public String getCookieValue(jakarta.servlet.http.HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}