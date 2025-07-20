package services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import models.EdgeNode;
import models.Location;
import models.Task;

/**
 * Service migration mechanism for Edge-Fog computing environments
 * Addresses the "No dynamic service migration" gap identified in the feedback
 */
public class ServiceMigration {
    
    // Map of migration sessions
    private Map<String, MigrationSession> migrationSessions;
    
    // Service discovery reference
    private ServiceDiscovery serviceDiscovery;
    
    /**
     * Create a new ServiceMigration instance with no service discovery
     */
    public ServiceMigration() {
        this.migrationSessions = new HashMap<>();
        this.serviceDiscovery = null;
    }
    
    /**
     * Create a new ServiceMigration instance
     * 
     * @param serviceDiscovery Service discovery mechanism to use
     */
    public ServiceMigration(ServiceDiscovery serviceDiscovery) {
        this.migrationSessions = new HashMap<>();
        this.serviceDiscovery = serviceDiscovery;
    }
    
    /**
     * Start a service migration from one edge node to another using node IDs
     * 
     * @param sourceNodeId ID of the source node
     * @param targetNodeId ID of the target node
     * @param serviceType Type of service being migrated
     * @param taskId ID of the task being migrated
     * @return Migration session ID or null if failed
     */
    public String startMigration(String sourceNodeId, String targetNodeId, String serviceType, String taskId) {
        try {
            // Create a simple migration session without service discovery validation
            String sessionId = UUID.randomUUID().toString();
            
            // Create dummy nodes for the session
            EdgeNode sourceNode = new EdgeNode(Integer.parseInt(sourceNodeId), "Node-" + sourceNodeId, 
                    new Location(0, 0), 1000, 1024, 2048, 1);
            EdgeNode targetNode = new EdgeNode(Integer.parseInt(targetNodeId), "Node-" + targetNodeId, 
                    new Location(0, 0), 1000, 1024, 2048, 1);
            
            // Create a migration session
            MigrationSession session = new MigrationSession(
                sessionId, serviceType + "-" + taskId, sourceNode, targetNode, MigrationPolicy.LIVE_MIGRATION
            );
            
            migrationSessions.put(sessionId, session);
            session.setState(MigrationState.PREPARING);
            
            // Simulate migration progress
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Simulate preparation time
                    updateMigrationState(sessionId, MigrationState.TRANSFERRING, null);
                    Thread.sleep(1000); // Simulate transfer time
                    updateMigrationState(sessionId, MigrationState.COMPLETED, null);
                } catch (InterruptedException e) {
                    updateMigrationState(sessionId, MigrationState.FAILED, null);
                }
            }).start();
            
            return sessionId;
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Start a service migration from one edge node to another
     * 
     * @param serviceId ID of the service to migrate
     * @param sourceNode Source edge node
     * @param targetNode Target edge node
     * @param migrationPolicy Policy to use for migration
     * @return Migration session ID
     */
    public String startMigration(String serviceId, EdgeNode sourceNode, EdgeNode targetNode, MigrationPolicy migrationPolicy) {
        // Check if service exists
        boolean serviceExists = false;
        for (ServiceDiscovery.ServiceRegistration service : serviceDiscovery.getAllServices()) {
            if (service.getServiceId().equals(serviceId) && service.getProvider().getId().equals(sourceNode.getId())) {
                serviceExists = true;
                break;
            }
        }
        
        if (!serviceExists) {
            throw new IllegalArgumentException("Service " + serviceId + " not found on source node " + sourceNode.getId());
        }
        
        // Create migration session
        String sessionId = UUID.randomUUID().toString();
        MigrationSession session = new MigrationSession(
            sessionId, serviceId, sourceNode, targetNode, migrationPolicy
        );
        
        migrationSessions.put(sessionId, session);
        
        // Start migration process
        session.setState(MigrationState.PREPARING);
        
        return sessionId;
    }
    
    /**
     * Update the state of a migration session
     * 
     * @param sessionId Migration session ID
     * @param newState New migration state
     * @param metadata Additional metadata about the state change
     * @return True if state was updated successfully
     */
    public boolean updateMigrationState(String sessionId, MigrationState newState, Map<String, Object> metadata) {
        if (!migrationSessions.containsKey(sessionId)) {
            return false;
        }
        
        MigrationSession session = migrationSessions.get(sessionId);
        session.setState(newState);
        
        if (metadata != null) {
            session.getMetadata().putAll(metadata);
        }
        
        // If migration is complete, update service registration
        if (newState == MigrationState.COMPLETED && serviceDiscovery != null) {
            for (ServiceDiscovery.ServiceRegistration service : serviceDiscovery.getAllServices()) {
                if (service.getServiceId().equals(session.getServiceId()) && 
                    service.getProvider().getId().equals(session.getSourceNode().getId())) {
                    
                    // Unregister from source node
                    serviceDiscovery.unregisterService(service.getServiceId());
                    
                    // Register on target node
                    serviceDiscovery.registerService(
                        service.getServiceId(),
                        service.getServiceName(),
                        service.getServiceType(),
                        session.getTargetNode(),
                        service.getMetadata()
                    );
                    
                    break;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Get the current state of a migration session
     * 
     * @param sessionId Migration session ID
     * @return Current migration state or null if session not found
     */
    public MigrationState getMigrationState(String sessionId) {
        if (!migrationSessions.containsKey(sessionId)) {
            return null;
        }
        
        return migrationSessions.get(sessionId).getState();
    }
    
    /**
     * Get a migration session by ID
     * 
     * @param sessionId Migration session ID
     * @return Migration session or null if not found
     */
    public MigrationSession getMigrationSession(String sessionId) {
        return migrationSessions.get(sessionId);
    }
    
    /**
     * Cancel a migration session
     * 
     * @param sessionId Migration session ID
     * @return True if session was found and canceled
     */
    public boolean cancelMigration(String sessionId) {
        if (!migrationSessions.containsKey(sessionId)) {
            return false;
        }
        
        MigrationSession session = migrationSessions.get(sessionId);
        session.setState(MigrationState.CANCELED);
        
        return true;
    }
    
    /**
     * Migration policy enum
     */
    public enum MigrationPolicy {
        COLD_MIGRATION,    // Stop service on source, transfer state, start on target
        WARM_MIGRATION,    // Prepare service on target, brief pause to transfer final state
        LIVE_MIGRATION     // No service interruption, continuous state transfer
    }
    
    /**
     * Migration state enum
     */
    public enum MigrationState {
        PREPARING,         // Initial preparation
        TRANSFERRING,      // Transferring service state
        ACTIVATING,        // Activating service on target node
        DEACTIVATING,      // Deactivating service on source node
        COMPLETED,         // Migration completed successfully
        FAILED,            // Migration failed
        CANCELED           // Migration was canceled
    }
    
    /**
     * Inner class representing a migration session
     */
    public static class MigrationSession {
        private String sessionId;
        private String serviceId;
        private EdgeNode sourceNode;
        private EdgeNode targetNode;
        private MigrationPolicy policy;
        private MigrationState state;
        private Map<String, Object> metadata;
        private long startTime;
        private long lastUpdateTime;
        
        public MigrationSession(String sessionId, String serviceId, EdgeNode sourceNode, 
                               EdgeNode targetNode, MigrationPolicy policy) {
            this.sessionId = sessionId;
            this.serviceId = serviceId;
            this.sourceNode = sourceNode;
            this.targetNode = targetNode;
            this.policy = policy;
            this.state = MigrationState.PREPARING;
            this.metadata = new HashMap<>();
            this.startTime = System.currentTimeMillis();
            this.lastUpdateTime = this.startTime;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public String getServiceId() {
            return serviceId;
        }
        
        public EdgeNode getSourceNode() {
            return sourceNode;
        }
        
        public EdgeNode getTargetNode() {
            return targetNode;
        }
        
        public MigrationPolicy getPolicy() {
            return policy;
        }
        
        public MigrationState getState() {
            return state;
        }
        
        public void setState(MigrationState state) {
            this.state = state;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public long getLastUpdateTime() {
            return lastUpdateTime;
        }
        
        public long getDuration() {
            return System.currentTimeMillis() - startTime;
        }
        
        @Override
        public String toString() {
            return "MigrationSession[id=" + sessionId + ", service=" + serviceId + 
                   ", source=" + sourceNode.getId() + ", target=" + targetNode.getId() + 
                   ", state=" + state + "]";
        }
    }
}
