package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents an Edge Computing node in the system
 */
public class EdgeNode {
    private int nodeId;
    private String nodeName;
    private Location location;
    private double mips;          // Processing capacity in Million Instructions Per Second
    private int ramMB;            // RAM in Megabytes
    private int storageMB;        // Storage in Megabytes
    private int resourceType;     // 1 = low resources, 2 = high resources
    private double cpuUtilization; // Current CPU utilization (0-100%)
    private Queue<Task> taskQueue; // Queue of tasks waiting to be processed
    private List<Task> completedTasks; // List of completed tasks
    
    /**
     * Full constructor for EdgeNode
     * @param nodeId Unique identifier for the edge node
     * @param nodeName Name of the edge node
     * @param location Physical location of the edge node
     * @param mips Processing capacity in Million Instructions Per Second
     * @param ramMB RAM in Megabytes
     * @param storageMB Storage in Megabytes
     * @param resourceType Type of resource (1 = low resources, 2 = high resources)
     */
    public EdgeNode(int nodeId, String nodeName, Location location, 
                   double mips, int ramMB, int storageMB, int resourceType) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.location = location;
        this.mips = mips;
        this.ramMB = ramMB;
        this.storageMB = storageMB;
        this.resourceType = resourceType;
        this.cpuUtilization = 0.0;
        this.taskQueue = new LinkedList<>();
        this.completedTasks = new ArrayList<>();
    }
    
    /**
     * Alternative constructor for EdgeNode with default resource type
     * @param nodeId Unique identifier for the edge node
     * @param nodeType Type of the edge node (used as name)
     * @param mips Processing capacity in Million Instructions Per Second
     * @param ramMB RAM in Megabytes
     * @param storageMB Storage in Megabytes
     * @param location Physical location of the edge node
     */
    public EdgeNode(int nodeId, String nodeType, double mips, int ramMB, int storageMB, Location location) {
        this(nodeId, nodeType, location, mips, ramMB, storageMB, nodeType.equals("type1") ? 1 : 2);
    }
    
    /**
     * Add a task to the processing queue
     * @param task The task to be added
     */
    public void queueTask(Task task) {
        taskQueue.add(task);
        task.setStatus(Task.TaskStatus.QUEUED);
    }
    
    /**
     * Process the next task in the queue
     * @param currentTime Current simulation time
     * @return The processed task, or null if no tasks in queue
     */
    public Task processNextTask(long currentTime) {
        if (taskQueue.isEmpty()) {
            return null;
        }
        
        Task task = taskQueue.poll();
        task.markStarted();
        
        // Simulate task execution
        double executionTime = task.calculateExecutionTime(mips);
        
        // Update CPU utilization
        updateCpuUtilization(task.getCpuDemand());
        
        // Mark task as completed
        task.markCompleted();
        completedTasks.add(task);
        
        return task;
    }
    
    /**
     * Update CPU utilization based on task demand
     * @param taskDemand CPU demand of the task
     */
    private void updateCpuUtilization(double taskDemand) {
        // Simple model: utilization is proportional to task demand
        // Assuming max task demand is 15000 MI
        double utilizationDelta = (taskDemand / 15000.0) * 50.0; // Max 50% increase
        
        // Update utilization with some decay from previous value
        cpuUtilization = (cpuUtilization * 0.7) + utilizationDelta;
        
        // Ensure utilization is between 0 and 100
        cpuUtilization = Math.max(0, Math.min(100, cpuUtilization));
    }
    
    /**
     * Calculate average service time for completed tasks
     * @return Average service time in milliseconds
     */
    public double calculateAverageServiceTime() {
        if (completedTasks.isEmpty()) {
            return 0.0;
        }
        
        long totalServiceTime = 0;
        for (Task task : completedTasks) {
            totalServiceTime += task.getActualServiceTime();
        }
        
        return (double) totalServiceTime / completedTasks.size();
    }
    
    /**
     * Check if the edge node can handle a specific task
     * @param task The task to check
     * @return True if the edge node can handle the task, false otherwise
     */
    public boolean canHandleTask(Task task) {
        // Check if CPU utilization is below threshold
        if (cpuUtilization > 90.0) {
            return false;
        }
        
        // Check if task queue is not too long
        if (taskQueue.size() > 20) {
            return false;
        }
        
        return true;
    }
    
    // Getters and setters
    public int getNodeId() {
        return nodeId;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public double getMips() {
        return mips;
    }
    
    public int getRamMB() {
        return ramMB;
    }
    
    public int getStorageMB() {
        return storageMB;
    }
    
    public int getResourceType() {
        return resourceType;
    }
    
    public double getCpuUtilization() {
        return cpuUtilization;
    }
    
    public void setCpuUtilization(double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }
    
    public int getQueueSize() {
        return taskQueue.size();
    }
    
    public List<Task> getCompletedTasks() {
        return completedTasks;
    }
}
