package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Utility class for secure configuration management and secret handling
 */
@Component
@Slf4j
public class SecureConfigUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final Pattern SECRET_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{32,}$"); // At least 32 chars with letters and numbers

    private final Environment environment;

    public SecureConfigUtil(Environment environment) {
        this.environment = environment;
    }

    /**
     * Validates if the secret meets security requirements
     */
    public static boolean isValidSecret(String secret) {
        return secret != null && SECRET_PATTERN.matcher(secret).matches();
    }

    /**
     * Generates a secure random secret key
     */
    public static String generateSecureSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("Error generating secure secret", e);
            // Fallback to random string
            byte[] randomBytes = new byte[32];
            new SecureRandom().nextBytes(randomBytes);
            return Base64.getEncoder().encodeToString(randomBytes);
        }
    }

    /**
     * Gets a configuration property with validation
     */
    public String getValidatedProperty(String key, String defaultValue) {
        String value = environment.getProperty(key, defaultValue);
        
        // Log if the property is a default and suggest using environment variables
        if (value != null && value.equals(defaultValue)) {
            log.warn("Using default value for property '{}'. Consider setting environment variable for production: {}", 
                    key, key.toUpperCase().replace('.', '_'));
        }
        
        return value;
    }

    /**
     * Gets a sensitive configuration property and validates it
     */
    public String getSensitiveProperty(String key, String defaultValue) {
        String value = environment.getProperty(key, defaultValue);
        
        if (value != null && !value.equals(defaultValue)) {
            log.info("Using custom value for sensitive property: {}", key);
        } else if (value != null && value.equals(defaultValue)) {
            log.warn("Using default value for sensitive property '{}'. This is insecure for production environments! " +
                    "Please set environment variable: {}", key, key.toUpperCase().replace('.', '_'));
        }
        
        // Validate secret strength if this is a secret property
        if (key.toLowerCase().contains("secret") || key.toLowerCase().contains("password") || 
            key.toLowerCase().contains("token")) {
            if (!isValidSecret(value)) {
                log.warn("Sensitive property '{}' does not meet security requirements. " +
                        "Should be at least 32 characters with letters, numbers, and special characters.", key);
            }
        }
        
        return value;
    }

    /**
     * Encrypts a sensitive value
     */
    public static String encryptValue(String value, String encryptionKey) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting value", e);
            return value; // Return original value if encryption fails
        }
    }

    /**
     * Decrypts an encrypted value
     */
    public static String decryptValue(String encryptedValue, String encryptionKey) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Error decrypting value", e);
            return encryptedValue; // Return encrypted value if decryption fails
        }
    }

    /**
     * Masks sensitive information in logs
     */
    public static String maskSensitiveInfo(String value) {
        if (value == null) {
            return null;
        }
        
        // Mask all but first and last 3 characters
        if (value.length() <= 6) {
            return value.replaceAll(".", "*");
        }
        
        String first = value.substring(0, 3);
        String last = value.substring(value.length() - 3);
        String middle = value.substring(3, value.length() - 3).replaceAll(".", "*");
        
        return first + middle + last;
    }

    /**
     * Validates that required properties are present
     */
    public void validateRequiredProperties(String... propertyNames) {
        for (String propertyName : propertyNames) {
            String value = environment.getProperty(propertyName);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalStateException(
                    String.format("Required property '%s' is not set. Please configure it via environment variable or application properties.", 
                    propertyName));
            }
        }
    }
}