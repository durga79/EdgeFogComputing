package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import models.EdgeNode;
import models.Task;

/**
 * Fault tolerance mechanism for Edge-Fog computing environments
 * Addresses the "No fault tolerance mechanisms" gap identified in the feedback
 */
public class FaultTolerance {
    
    /**
     * Fault tolerance levels for tasks
     */
    public enum FaultToleranceLevel {
        NONE,           // No fault tolerance
        CHECKPOINTING,  // Periodic checkpointing
        REPLICATION     // Task replication
    }
    
    /**
     * Health status of edge nodes
     */
    public enum HealthStatus {
        HEALTHY,        // Node is healthy
        DEGRADED,       // Node is experiencing performance issues
        UNHEALTHY,      // Node is unhealthy but still operational
        FAILED,         // Node has failed completely
        UNKNOWN         // Node status is unknown
    }
    
    // Health status of edge nodes
    private Map<String, NodeHealth> nodeHealthMap;
    
    // Task replication map (task ID -> replicated task IDs)
    private Map<String, List<String>> taskReplicationMap;
    
    // Task checkpoints (task ID -> checkpoint data)
    private Map<String, List<TaskCheckpoint>> taskCheckpoints;
    
    // Service discovery reference
    private ServiceDiscovery serviceDiscovery;
    
    /**
     * Create a new FaultTolerance instance with no service discovery
     */
    public FaultTolerance() {
        this.nodeHealthMap = new ConcurrentHashMap<>();
        this.taskReplicationMap = new ConcurrentHashMap<>();
        this.taskCheckpoints = new ConcurrentHashMap<>();
        this.serviceDiscovery = null;
    }
    
    /**
     * Create a new FaultTolerance instance
     * 
     * @param serviceDiscovery Service discovery mechanism to use
     */
    public FaultTolerance(ServiceDiscovery serviceDiscovery) {
        this.nodeHealthMap = new ConcurrentHashMap<>();
        this.taskReplicationMap = new ConcurrentHashMap<>();
        this.taskCheckpoints = new ConcurrentHashMap<>();
        this.serviceDiscovery = serviceDiscovery;
    }
    
    /**
     * Update the health status of an edge node
     * 
     * @param node Edge node to update
     * @param status Health status
     * @param metrics Health metrics
     */
    public void updateNodeHealth(EdgeNode node, HealthStatus status, Map<String, Object> metrics) {
        NodeHealth health = nodeHealthMap.getOrDefault(node.getId(), new NodeHealth(node.getId()));
        health.setStatus(status);
        health.setLastUpdateTime(System.currentTimeMillis());
        
        if (metrics != null) {
            health.getMetrics().putAll(metrics);
        }
        
        nodeHealthMap.put(node.getId(), health);
        
        // If node is unhealthy, trigger recovery actions
        if (status == HealthStatus.UNHEALTHY || status == HealthStatus.FAILED) {
            triggerRecoveryActions(node);
        }
    }
    
    /**
     * Get the health status of an edge node
     * 
     * @param nodeId ID of the edge node
     * @return Health status or null if not found
     */
    public HealthStatus getNodeHealthStatus(String nodeId) {
        if (!nodeHealthMap.containsKey(nodeId)) {
            return null;
        }
        
        return nodeHealthMap.get(nodeId).getStatus();
    }
    
    /**
     * Register a task for fault tolerance
     * 
     * @param task Task to register
     * @param toleranceLevel Fault tolerance level
     * @return True if registration was successful
     */
    public boolean registerTask(Task task, FaultToleranceLevel toleranceLevel) {
        switch (toleranceLevel) {
            case NONE:
                // No fault tolerance, just return true
                return true;
                
            case CHECKPOINTING:
                // Initialize checkpoint list
                if (!taskCheckpoints.containsKey(task.getId())) {
                    taskCheckpoints.put(task.getId(), new ArrayList<>());
                }
                return true;
                
            case REPLICATION:
                // Create a replicated task
                String replicaId = createTaskReplica(task);
                if (replicaId != null) {
                    if (!taskReplicationMap.containsKey(task.getId())) {
                        taskReplicationMap.put(task.getId(), new ArrayList<>());
                    }
                    taskReplicationMap.get(task.getId()).add(replicaId);
                    return true;
                }
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * Create a checkpoint for a task
     * 
     * @param taskId ID of the task
     * @param checkpointData Checkpoint data
     * @return Checkpoint ID
     */
    public String createCheckpoint(String taskId, Map<String, Object> checkpointData) {
        if (!taskCheckpoints.containsKey(taskId)) {
            taskCheckpoints.put(taskId, new ArrayList<>());
        }
        
        String checkpointId = UUID.randomUUID().toString();
        TaskCheckpoint checkpoint = new TaskCheckpoint(checkpointId, taskId, checkpointData);
        
        taskCheckpoints.get(taskId).add(checkpoint);
        
        return checkpointId;
    }
    
    /**
     * Get the latest checkpoint for a task
     * 
     * @param taskId ID of the task
     * @return Latest checkpoint or null if none exists
     */
    public TaskCheckpoint getLatestCheckpoint(String taskId) {
        if (!taskCheckpoints.containsKey(taskId) || taskCheckpoints.get(taskId).isEmpty()) {
            return null;
        }
        
        List<TaskCheckpoint> checkpoints = taskCheckpoints.get(taskId);
        return checkpoints.get(checkpoints.size() - 1);
    }
    
    /**
     * Recover a task from its latest checkpoint
     * 
     * @param taskId ID of the task
     * @param targetNode Target edge node for recovery
     * @return True if recovery was successful
     */
    public boolean recoverTaskFromCheckpoint(String taskId, EdgeNode targetNode) {
        TaskCheckpoint checkpoint = getLatestCheckpoint(taskId);
        if (checkpoint == null) {
            return false;
        }
        
        // In a real implementation, this would restore the task state on the target node
        // For simulation purposes, we just return true
        return true;
    }
    
    /**
     * Create a replica of a task on another edge node
     * 
     * @param task Task to replicate
     * @return ID of the replicated task or null if replication failed
     */
    private String createTaskReplica(Task task) {
        // In a real implementation, this would create a replica of the task on another edge node
        // For simulation purposes, we just return a new UUID
        return UUID.randomUUID().toString();
    }
    
    /**
     * Trigger recovery actions for a failed node
     * 
     * @param node Failed edge node
     */
    private void triggerRecoveryActions(EdgeNode node) {
        // In a real implementation, this would:
        // 1. Identify affected tasks and services
        // 2. Migrate services to healthy nodes
        // 3. Recover tasks from checkpoints or replicas
        // For simulation purposes, we just log the action
        System.out.println("Triggering recovery actions for node " + node.getId());
    }
    
    /**
     * Create a checkpoint for a task
     * 
     * @param task Task to checkpoint
     * @return True if checkpoint was created successfully
     */
    public boolean createTaskCheckpoint(Task task) {
        if (task == null) return false;
        
        String taskId = task.getId();
        if (!taskCheckpoints.containsKey(taskId)) {
            taskCheckpoints.put(taskId, new ArrayList<>());
        }
        
        // Create a checkpoint with current task state
        Map<String, Object> checkpointData = new HashMap<>();
        checkpointData.put("status", task.getStatus());
        checkpointData.put("executionLocation", task.getExecutionLocation());
        
        String checkpointId = createCheckpoint(taskId, checkpointData);
        return checkpointId != null;
    }
    
    /**
     * Handle a node failure
     * 
     * @param nodeId ID of the failed node
     * @param taskQueue Queue of tasks that were on the node
     * @return True if failure was handled successfully
     */
    public boolean handleNodeFailure(String nodeId, Queue<Task> taskQueue) {
        // Update node health status
        NodeHealth health = nodeHealthMap.getOrDefault(nodeId, new NodeHealth(nodeId));
        health.setStatus(HealthStatus.FAILED);
        health.setLastUpdateTime(System.currentTimeMillis());
        nodeHealthMap.put(nodeId, health);
        
        // Log the failure
        System.out.println("Node " + nodeId + " has failed. Handling " + taskQueue.size() + " affected tasks.");
        
        // For each task in the queue, mark it for recovery
        for (Task task : taskQueue) {
            // Add to recovery list
            if (!taskCheckpoints.containsKey(task.getId())) {
                // No checkpoints available, can't recover
                System.out.println("Task " + task.getId() + " has no checkpoints, cannot recover.");
                continue;
            }
            
            // Mark task as failed but recoverable
            task.markFailed();
            System.out.println("Task " + task.getId() + " marked for recovery.");
        }
        
        return true;
    }
    
    /**
     * Recover tasks from a failed node
     * 
     * @param nodeId ID of the failed node
     * @return List of recovered tasks
     */
    public List<Task> recoverTasks(String nodeId) {
        List<Task> recoveredTasks = new ArrayList<>();
        
        // In a real implementation, this would recover tasks from checkpoints
        // For simulation purposes, we just return an empty list
        System.out.println("Attempting to recover tasks from node " + nodeId);
        
        return recoveredTasks;
    }
    
    // Health status enum is defined above
    
    /**
     * Inner class representing node health information
     */
    public static class NodeHealth {
        private String nodeId;
        private HealthStatus status;
        private Map<String, Object> metrics;
        private long lastUpdateTime;
        
        public NodeHealth(String nodeId) {
            this.nodeId = nodeId;
            this.status = HealthStatus.UNKNOWN;
            this.metrics = new HashMap<>();
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public String getNodeId() {
            return nodeId;
        }
        
        public HealthStatus getStatus() {
            return status;
        }
        
        public void setStatus(HealthStatus status) {
            this.status = status;
        }
        
        public Map<String, Object> getMetrics() {
            return metrics;
        }
        
        public long getLastUpdateTime() {
            return lastUpdateTime;
        }
        
        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
        
        @Override
        public String toString() {
            return "NodeHealth[nodeId=" + nodeId + ", status=" + status + 
                   ", lastUpdate=" + lastUpdateTime + "]";
        }
    }
    
    /**
     * Inner class representing a task checkpoint
     */
    public static class TaskCheckpoint {
        private String checkpointId;
        private String taskId;
        private Map<String, Object> checkpointData;
        private long timestamp;
        
        public TaskCheckpoint(String checkpointId, String taskId, Map<String, Object> checkpointData) {
            this.checkpointId = checkpointId;
            this.taskId = taskId;
            this.checkpointData = checkpointData != null ? checkpointData : new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getCheckpointId() {
            return checkpointId;
        }
        
        public String getTaskId() {
            return taskId;
        }
        
        public Map<String, Object> getCheckpointData() {
            return checkpointData;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return "TaskCheckpoint[id=" + checkpointId + ", taskId=" + taskId + 
                   ", timestamp=" + timestamp + "]";
        }
    }
}
