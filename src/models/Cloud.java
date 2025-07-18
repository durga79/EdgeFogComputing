package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents the Cloud Computing resources in the system
 */
public class Cloud {
    private String cloudId;
    private double mips;          // Processing capacity in Million Instructions Per Second
    private int ramMB;            // RAM in Megabytes
    private int storageMB;        // Storage in Megabytes
    private double wanLatency;    // WAN latency to edge nodes in milliseconds
    private double wanBandwidth;  // WAN bandwidth to edge nodes in Mbps
    private double cpuUtilization; // Current CPU utilization (0-100%)
    private Queue<Task> taskQueue; // Queue of tasks waiting to be processed
    private List<Task> completedTasks; // List of completed tasks
    
    /**
     * Full constructor for Cloud
     * @param cloudId Unique identifier for the cloud
     * @param mips Processing capacity in Million Instructions Per Second
     * @param ramMB RAM in Megabytes
     * @param storageMB Storage in Megabytes
     * @param wanLatency WAN latency to edge nodes in milliseconds
     * @param wanBandwidth WAN bandwidth to edge nodes in Mbps
     */
    public Cloud(String cloudId, double mips, int ramMB, int storageMB, 
                double wanLatency, double wanBandwidth) {
        this.cloudId = cloudId;
        this.mips = mips;
        this.ramMB = ramMB;
        this.storageMB = storageMB;
        this.wanLatency = wanLatency;
        this.wanBandwidth = wanBandwidth;
        this.cpuUtilization = 0.0;
        this.taskQueue = new LinkedList<>();
        this.completedTasks = new ArrayList<>();
    }
    
    /**
     * Alternative constructor for Cloud with default cloudId
     * @param mips Processing capacity in Million Instructions Per Second
     * @param ramMB RAM in Megabytes
     * @param storageMB Storage in Megabytes
     * @param wanLatency WAN latency to edge nodes in milliseconds
     * @param wanBandwidth WAN bandwidth to edge nodes in Mbps
     */
    public Cloud(double mips, int ramMB, int storageMB, double wanLatency, double wanBandwidth) {
        this("cloud-1", mips, ramMB, storageMB, wanLatency, wanBandwidth);
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
        
        // Add WAN latency to the execution time
        executionTime += wanLatency * 2; // Round trip latency
        
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
        double utilizationDelta = (taskDemand / 15000.0) * 20.0; // Max 20% increase (cloud has more capacity)
        
        // Update utilization with some decay from previous value
        cpuUtilization = (cpuUtilization * 0.8) + utilizationDelta;
        
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
     * Calculate the total service time for a task including transfer and execution
     * @param task The task to calculate service time for
     * @return Total service time in milliseconds
     */
    public double calculateTaskServiceTime(Task task) {
        // Calculate transfer time based on WAN bandwidth
        double transferTime = task.calculateTransferTime(wanBandwidth);
        
        // Add WAN latency (round trip)
        transferTime += wanLatency * 2;
        
        // Calculate execution time
        double executionTime = task.calculateExecutionTime(mips);
        
        return transferTime + executionTime;
    }
    
    // Getters and setters
    public String getCloudId() {
        return cloudId;
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
    
    public double getWanLatency() {
        return wanLatency;
    }
    
    public double getWanBandwidth() {
        return wanBandwidth;
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
