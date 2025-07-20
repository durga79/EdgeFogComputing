package security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Security manager for Edge-Fog computing environments
 * Addresses the "No security mechanisms" gap identified in the feedback
 */
public class SecurityManager {
    
    // Authentication tokens (token -> user ID)
    private Map<String, String> authTokens;
    
    // User credentials (user ID -> credentials)
    private Map<String, UserCredentials> userCredentials;
    
    // Session information (token -> session)
    private Map<String, Session> sessions;
    
    public SecurityManager() {
        this.authTokens = new HashMap<>();
        this.userCredentials = new HashMap<>();
        this.sessions = new HashMap<>();
    }
    
    /**
     * Register a new user
     * 
     * @param userId User ID
     * @param password User password
     * @param role User role
     * @return True if registration was successful
     */
    public boolean registerUser(String userId, String password, UserRole role) {
        if (userCredentials.containsKey(userId)) {
            return false; // User already exists
        }
        
        // In a real implementation, the password would be hashed
        String hashedPassword = hashPassword(password);
        
        UserCredentials credentials = new UserCredentials(userId, hashedPassword, role);
        userCredentials.put(userId, credentials);
        
        return true;
    }
    
    /**
     * Authenticate a user and create a session
     * 
     * @param userId User ID
     * @param password User password
     * @return Authentication token or null if authentication failed
     */
    public String authenticate(String userId, String password) {
        if (!userCredentials.containsKey(userId)) {
            return null; // User does not exist
        }
        
        UserCredentials credentials = userCredentials.get(userId);
        
        // In a real implementation, the password would be hashed and compared
        String hashedPassword = hashPassword(password);
        
        if (!credentials.getHashedPassword().equals(hashedPassword)) {
            return null; // Incorrect password
        }
        
        // Generate a token
        String token = generateToken();
        
        // Create a session
        Session session = new Session(token, userId, System.currentTimeMillis());
        
        // Store token and session
        authTokens.put(token, userId);
        sessions.put(token, session);
        
        return token;
    }
    
    /**
     * Validate an authentication token
     * 
     * @param token Authentication token
     * @return True if token is valid
     */
    public boolean validateToken(String token) {
        if (!authTokens.containsKey(token)) {
            return false; // Token does not exist
        }
        
        Session session = sessions.get(token);
        
        // Check if session has expired (1 hour expiration)
        long currentTime = System.currentTimeMillis();
        if (currentTime - session.getCreationTime() > 3600000) {
            // Session expired, remove token and session
            authTokens.remove(token);
            sessions.remove(token);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the user ID associated with a token
     * 
     * @param token Authentication token
     * @return User ID or null if token is invalid
     */
    public String getUserIdFromToken(String token) {
        if (!validateToken(token)) {
            return null;
        }
        
        return authTokens.get(token);
    }
    
    /**
     * Check if a user has a specific permission
     * 
     * @param userId User ID
     * @param permission Permission to check
     * @return True if user has the permission
     */
    public boolean hasPermission(String userId, Permission permission) {
        if (!userCredentials.containsKey(userId)) {
            return false; // User does not exist
        }
        
        UserCredentials credentials = userCredentials.get(userId);
        UserRole role = credentials.getRole();
        
        // Check if role has the permission
        switch (role) {
            case ADMIN:
                // Admin has all permissions
                return true;
                
            case USER:
                // User has limited permissions
                return permission == Permission.READ || 
                       permission == Permission.EXECUTE;
                
            case GUEST:
                // Guest has only read permission
                return permission == Permission.READ;
                
            default:
                return false;
        }
    }
    
    /**
     * Invalidate a token (logout)
     * 
     * @param token Authentication token
     */
    public void invalidateToken(String token) {
        authTokens.remove(token);
        sessions.remove(token);
    }
    
    /**
     * Generate a secure random token
     * 
     * @return Random token
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a secure random token for a device
     * 
     * @param deviceId ID of the device
     * @return Random token
     */
    public String generateToken(int deviceId) {
        // Create a token that includes the device ID for traceability
        String baseToken = generateToken();
        return baseToken + "-" + deviceId;
    }
    
    /**
     * Hash a password (simplified for simulation)
     * 
     * @param password Password to hash
     * @return Hashed password
     */
    private String hashPassword(String password) {
        // In a real implementation, this would use a secure hashing algorithm
        // For simulation purposes, we just return a simple hash
        return String.valueOf(password.hashCode());
    }
    
    /**
     * User role enum
     */
    public enum UserRole {
        ADMIN,  // Administrator with full access
        USER,   // Regular user with limited access
        GUEST   // Guest with minimal access
    }
    
    /**
     * Permission enum
     */
    public enum Permission {
        READ,    // Permission to read data
        WRITE,   // Permission to write data
        EXECUTE, // Permission to execute operations
        ADMIN    // Administrative permission
    }
    
    /**
     * Inner class representing user credentials
     */
    private static class UserCredentials {
        private String userId;
        private String hashedPassword;
        private UserRole role;
        
        public UserCredentials(String userId, String hashedPassword, UserRole role) {
            this.userId = userId;
            this.hashedPassword = hashedPassword;
            this.role = role;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getHashedPassword() {
            return hashedPassword;
        }
        
        public UserRole getRole() {
            return role;
        }
    }
    
    /**
     * Inner class representing a session
     */
    private static class Session {
        private String token;
        private String userId;
        private long creationTime;
        
        public Session(String token, String userId, long creationTime) {
            this.token = token;
            this.userId = userId;
            this.creationTime = creationTime;
        }
        
        public String getToken() {
            return token;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public long getCreationTime() {
            return creationTime;
        }
    }
}
