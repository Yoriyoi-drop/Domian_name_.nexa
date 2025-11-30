package com.myproject.nexa.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private InputSanitizer inputSanitizer;

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil(inputSanitizer);
    }

    @Test
    void testSanitizeUserInput() {
        String input = "<script>alert('xss')</script> normal text";
        String sanitizedInput = " normal text";

        when(inputSanitizer.stripHtml(anyString())).thenReturn(sanitizedInput);

        String result = securityUtil.sanitizeUserInput(input);
        assertEquals(sanitizedInput, result);
    }

    @Test
    void testSanitizeUserInputWithNull() {
        String result = securityUtil.sanitizeUserInput(null);
        assertNull(result);
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
    void testPasswordTooCommon() {
        assertTrue(securityUtil.isPasswordTooCommon("password123"));
        assertTrue(securityUtil.isPasswordTooCommon("PaSsWoRd"));
        assertFalse(securityUtil.isPasswordTooCommon("SecureP@ssw0rd2023!"));
        assertFalse(securityUtil.isPasswordTooCommon(null));
    }

    @Test
    void testValidFileName() {
        when(inputSanitizer.sanitizeFilename("valid_file.txt")).thenReturn("valid_file.txt");
        when(inputSanitizer.sanitizeFilename("../invalid_file.txt")).thenReturn("invalid_file.txt");

        assertTrue(securityUtil.isValidFileName("valid_file.txt"));
        assertFalse(securityUtil.isValidFileName("../invalid_file.txt"));
    }
}