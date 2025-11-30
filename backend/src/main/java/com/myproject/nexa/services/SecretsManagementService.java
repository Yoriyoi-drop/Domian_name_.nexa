package com.myproject.nexa.services;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for managing secrets with encryption, simulating HashiCorp Vault functionality
 * In a real implementation, this would connect to external vault systems
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecretsManagementService {

    private final AppProperties appProperties;
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int AES_KEY_LENGTH = 256; // 256 bits

    /**
     * Encrypt a secret value
     */
    public String encryptSecret(String secretValue) throws Exception {
        if (secretValue == null) {
            return null;
        }

        // Get the master encryption key from app properties (in real implementation, this would come from vault)
        byte[] masterKey = getMasterKeyBytes();
        SecretKey key = new SecretKeySpec(masterKey, 0, masterKey.length, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit auth tag
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted = cipher.doFinal(secretValue.getBytes("UTF-8"));
        
        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        // Base64 encode the result
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypt a secret value
     */
    public String decryptSecret(String encryptedSecret) throws Exception {
        if (encryptedSecret == null) {
            return null;
        }

        byte[] combined = Base64.getDecoder().decode(encryptedSecret);
        
        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

        // Get the master encryption key
        byte[] masterKey = getMasterKeyBytes();
        SecretKey key = new SecretKeySpec(masterKey, 0, masterKey.length, ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

    /**
     * Generate a new master key (for demonstration purposes)
     */
    public String generateMasterKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(AES_KEY_LENGTH);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Get master key bytes from configuration
     */
    private byte[] getMasterKeyBytes() {
        String masterKey = appProperties.getJwt().getSecret(); // Using JWT secret as master key for simulation
        if (masterKey == null || masterKey.isEmpty()) {
            throw new RuntimeException("Master key not configured");
        }
        
        // Pad or truncate to 32 bytes for AES-256
        byte[] keyBytes = masterKey.getBytes();
        if (keyBytes.length < 32) {
            // Pad with zeros
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return padded;
        } else if (keyBytes.length > 32) {
            // Truncate to 32 bytes
            byte[] truncated = new byte[32];
            System.arraycopy(keyBytes, 0, truncated, 0, 32);
            return truncated;
        }
        return keyBytes;
    }

    /**
     * Store a secret in the vault (simulated)
     */
    public void storeSecret(String key, String value) {
        try {
            String encryptedValue = encryptSecret(value);
            // In a real implementation, this would call Vault API to store the secret
            log.info("Secret stored in vault with key: {}", key);
        } catch (Exception e) {
            log.error("Error storing secret in vault: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store secret", e);
        }
    }

    /**
     * Retrieve a secret from the vault (simulated)
     */
    public String retrieveSecret(String key) {
        try {
            // In a real implementation, this would call Vault API to retrieve the secret
            // For simulation, we'll return a dummy encrypted value
            String encryptedValue = getSimulatedEncryptedValue(key);
            return decryptSecret(encryptedValue);
        } catch (Exception e) {
            log.error("Error retrieving secret from vault: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    /**
     * Simulate getting an encrypted value (in real implementation, this comes from Vault)
     */
    private String getSimulatedEncryptedValue(String key) {
        // This is a simulation - in real implementation, fetch from Vault
        // For demonstration, return a pre-encrypted version of a default value
        try {
            String defaultValue = "default_secret_for_" + key;
            return encryptSecret(defaultValue);
        } catch (Exception e) {
            log.error("Error encrypting default value: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Rotate secrets - update encryption keys
     */
    public void rotateSecrets(String[] keysToRotate) {
        log.info("Starting secrets rotation for {} keys", keysToRotate.length);
        for (String key : keysToRotate) {
            try {
                String currentSecret = retrieveSecret(key);
                // Store with new encryption
                storeSecret(key, currentSecret);
                log.info("Secret rotated for key: {}", key);
            } catch (Exception e) {
                log.error("Error rotating secret for key {}: {}", key, e.getMessage(), e);
            }
        }
        log.info("Secrets rotation completed");
    }
}