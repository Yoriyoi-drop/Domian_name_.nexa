package com.myproject.nexa.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil();
    }

    @Test
    void testSanitizeHtml() {
        String input = "<script>alert('xss')</script>";
        String expected = "&lt;script&gt;alert&#40;'xss'&#41;&lt;/script&gt;";
        String result = securityUtil.sanitizeHtml(input);
        assertEquals(expected, result);
    }

    @Test
    void testSanitizeHtmlWithNull() {
        String result = securityUtil.sanitizeHtml(null);
        assertNull(result);
    }

    @Test
    void testSanitizeSql() {
        String input = "'; DROP TABLE users; --";
        String expected = " DROP TABLE users; ";
        String result = securityUtil.sanitizeSql(input);
        assertEquals(expected, result);
    }

    @Test
    void testValidEmail() {
        assertTrue(securityUtil.isValidEmail("test@example.com"));
        assertFalse(securityUtil.isValidEmail("invalid-email"));
        assertFalse(securityUtil.isValidEmail(null));
    }

    @Test
    void testValidUsername() {
        assertTrue(securityUtil.isValidUsername("valid_user123"));
        assertFalse(securityUtil.isValidUsername("invalid user"));
        assertFalse(securityUtil.isValidUsername("x")); // Too short
        assertFalse(securityUtil.isValidUsername(null));
    }

    @Test
    void testContainsMaliciousContent() {
        assertTrue(securityUtil.containsMaliciousContent("SELECT * FROM users"));
        assertTrue(securityUtil.containsMaliciousContent("<script>alert</script>"));
        assertFalse(securityUtil.containsMaliciousContent("normal text"));
        assertFalse(securityUtil.containsMaliciousContent(null));
    }

    @Test
    void testEncodeForDisplay() {
        String input = "<script>alert('xss')</script>";
        String expected = "&lt;script&gt;alert&#40;'xss'&#41;&lt;/script&gt;";
        String result = securityUtil.encodeForDisplay(input);
        assertEquals(expected, result);
    }

    @Test
    void testSanitizeUserInput() {
        String input = "<script>alert('xss')</script> normal text";
        String expected = " normal text";
        String result = securityUtil.sanitizeUserInput(input);
        assertEquals(expected, result);
    }

    @Test
    void testSanitizeUserInputWithMaliciousContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            securityUtil.sanitizeUserInput("UNION SELECT * FROM users");
        });
    }
}