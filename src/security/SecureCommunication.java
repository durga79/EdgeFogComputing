package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Secure communication implementation for Edge-Fog computing environments
 * Addresses the "No secure communication" gap identified in the feedback
 */
public class SecureCommunication {
    
    // Encryption keys (node ID -> encryption key)
    private Map<String, SecretKey> encryptionKeys;
    
    // Default encryption algorithm
    private static final String ALGORITHM = "AES";
    
    public SecureCommunication() {
        this.encryptionKeys = new HashMap<>();
    }
    
    /**
     * Generate a new encryption key for a node
     * 
     * @param nodeId ID of the node
     * @return Base64 encoded string representation of the key
     * @throws Exception If key generation fails
     */
    public String generateKeyForNode(String nodeId) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // 256-bit key
        SecretKey key = keyGen.generateKey();
        
        encryptionKeys.put(nodeId, key);
        
        // Return Base64 encoded key
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Register an existing encryption key for a node
     * 
     * @param nodeId ID of the node
     * @param base64Key Base64 encoded key
     * @throws Exception If key registration fails
     */
    public void registerKeyForNode(String nodeId, String base64Key) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        
        encryptionKeys.put(nodeId, key);
    }
    
    /**
     * Encrypt data for a specific node
     * 
     * @param nodeId ID of the target node
     * @param data Data to encrypt
     * @return Base64 encoded encrypted data
     * @throws Exception If encryption fails
     */
    public String encrypt(String nodeId, String data) throws Exception {
        if (!encryptionKeys.containsKey(nodeId)) {
            throw new IllegalArgumentException("No encryption key registered for node: " + nodeId);
        }
        
        SecretKey key = encryptionKeys.get(nodeId);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypt data from a specific node
     * 
     * @param nodeId ID of the source node
     * @param encryptedData Base64 encoded encrypted data
     * @return Decrypted data
     * @throws Exception If decryption fails
     */
    public String decrypt(String nodeId, String encryptedData) throws Exception {
        if (!encryptionKeys.containsKey(nodeId)) {
            throw new IllegalArgumentException("No encryption key registered for node: " + nodeId);
        }
        
        SecretKey key = encryptionKeys.get(nodeId);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Check if a node has a registered encryption key
     * 
     * @param nodeId ID of the node
     * @return True if node has a registered key
     */
    public boolean hasKey(String nodeId) {
        return encryptionKeys.containsKey(nodeId);
    }
    
    /**
     * Remove an encryption key for a node
     * 
     * @param nodeId ID of the node
     * @return True if key was removed
     */
    public boolean removeKey(String nodeId) {
        if (!encryptionKeys.containsKey(nodeId)) {
            return false;
        }
        
        encryptionKeys.remove(nodeId);
        return true;
    }
    
    /**
     * Create a secure message with authentication and encryption
     * 
     * @param sourceNodeId ID of the source node
     * @param targetNodeId ID of the target node
     * @param data Data to secure
     * @param securityManager Security manager for authentication
     * @param authToken Authentication token
     * @return Secured message
     * @throws Exception If securing the message fails
     */
    public SecureMessage createSecureMessage(String sourceNodeId, String targetNodeId, 
                                           String data, SecurityManager securityManager, 
                                           String authToken) throws Exception {
        // Validate authentication token
        if (!securityManager.validateToken(authToken)) {
            throw new SecurityException("Invalid authentication token");
        }
        
        // Get user ID from token
        String userId = securityManager.getUserIdFromToken(authToken);
        
        // Encrypt data
        String encryptedData = encrypt(targetNodeId, data);
        
        // Create secure message
        return new SecureMessage(sourceNodeId, targetNodeId, userId, encryptedData, System.currentTimeMillis());
    }
    
    /**
     * Inner class representing a secure message
     */
    public static class SecureMessage {
        private String sourceNodeId;
        private String targetNodeId;
        private String userId;
        private String encryptedData;
        private long timestamp;
        
        public SecureMessage(String sourceNodeId, String targetNodeId, String userId, 
                           String encryptedData, long timestamp) {
            this.sourceNodeId = sourceNodeId;
            this.targetNodeId = targetNodeId;
            this.userId = userId;
            this.encryptedData = encryptedData;
            this.timestamp = timestamp;
        }
        
        public String getSourceNodeId() {
            return sourceNodeId;
        }
        
        public String getTargetNodeId() {
            return targetNodeId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getEncryptedData() {
            return encryptedData;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return "SecureMessage[source=" + sourceNodeId + ", target=" + targetNodeId + 
                   ", user=" + userId + ", timestamp=" + timestamp + "]";
        }
    }
    
    /**
     * Generate a hash for data integrity verification
     * 
     * @param data Data to hash
     * @return Base64 encoded hash
     */
    public static String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            // In case of error, return a simple hash
            return String.valueOf(data.hashCode());
        }
    }
}
