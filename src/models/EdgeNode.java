package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import services.ServiceDiscovery;
import services.FaultTolerance;
import bigdata.DataProcessor;
import bigdata.DataAggregator;
import bigdata.StreamingAnalytics;

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
    
    // Wireless communication components
    private Map<String, WirelessProtocol> supportedProtocols;
    private double signalStrength; // Base signal strength (0.0-1.0)
    
    // Energy components
    private EnergyModel energyModel;
    private double totalEnergyConsumed;
    
    // Service management components
    private ServiceDiscovery serviceDiscovery;
    private FaultTolerance faultTolerance;
    private boolean isHealthy;
    
    // Big data analytics components
    private DataProcessor streamingAnalytics;
    private DataProcessor dataAggregator;
    
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
        
        // Initialize wireless protocols
        this.supportedProtocols = new HashMap<>();
        this.supportedProtocols.put("WIFI", WirelessProtocolFactory.getProtocol("WIFI"));
        this.supportedProtocols.put("LORA", WirelessProtocolFactory.getProtocol("LORA"));
        this.signalStrength = 0.9; // Default strong signal
        
        // Initialize energy model
        this.energyModel = new LinearEnergyModel();
        this.totalEnergyConsumed = 0.0;
        
        // Initialize service management
        this.serviceDiscovery = new ServiceDiscovery();
        this.faultTolerance = new FaultTolerance();
        this.isHealthy = true;
        
        // Initialize big data analytics
        this.streamingAnalytics = new StreamingAnalytics(100); // Default window size of 100
        this.dataAggregator = new DataAggregator(60); // Default aggregation interval of 60 seconds
        
        // Register this node's services
        registerDefaultServices();
    }
    
    /**
     * Get the node ID
     * @return Node ID
     */
    public String getId() {
        return String.valueOf(nodeId);
    }
    
    /**
     * Register default services provided by this edge node
     */
    private void registerDefaultServices() {
        // Register basic services based on node capabilities
        Map<String, Object> serviceMetadata = new HashMap<>();
        serviceMetadata.put("mips", mips);
        serviceMetadata.put("ram", ramMB);
        serviceMetadata.put("storage", storageMB);
        
        // Register computation service
        serviceDiscovery.registerService(
            "computation_" + nodeId, 
            "Provides general computation capabilities",
            "COMPUTATION",
            this,
            serviceMetadata
        );
        
        // Register data processing service if node has sufficient resources
        if (resourceType == 2) { // High resource node
            serviceDiscovery.registerService(
                "data_analytics_" + nodeId, 
                "Provides data analytics capabilities",
                "ANALYTICS",
                this,
                serviceMetadata
            );
        }
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
        if (taskQueue.isEmpty() || !isHealthy) {
            return null;
        }
        
        Task task = taskQueue.poll();
        task.markStarted();
        
        // Check if task is secure (if it has security metadata)
        if (task.hasSecurityMetadata() && !task.verifyIntegrity()) {
            System.out.println("Security check failed for task " + task.getTaskId());
            task.setStatus(Task.TaskStatus.FAILED);
            return task;
        }
        
        // Create checkpoint for fault tolerance
        faultTolerance.createTaskCheckpoint(task);
        
        // Simulate task execution
        double executionTime = task.calculateExecutionTime(mips);
        
        // Calculate and consume energy for computation
        double computationEnergy = energyModel.calculateComputationEnergy(
            task.getCpuDemand(), (long)executionTime, mips);
        totalEnergyConsumed += computationEnergy;
        
        // Process any data generated by the task using big data analytics
        if (task.hasGeneratedData()) {
            processTaskData(task);
        }
        
        // Update CPU utilization
        updateCpuUtilization(task.getCpuDemand());
        
        // Mark task as completed
        task.markCompleted();
        completedTasks.add(task);
        
        return task;
    }
    
    /**
     * Process data generated by a task using big data analytics
     * @param task The task that generated data
     */
    private void processTaskData(Task task) {
        if (task.hasGeneratedData()) {
            // Use streaming analytics for real-time processing
            streamingAnalytics.processDataPoint(task.getGeneratedData());
            
            // Aggregate data over time windows
            dataAggregator.processDataPoint(task.getGeneratedData());
        }
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
    
    /**
     * Calculate bandwidth to a device based on protocol and distance
     * @param device The IoT device
     * @param protocolName The wireless protocol to use
     * @return The available bandwidth in Kbps
     */
    public double calculateBandwidthToDevice(IoTDevice device, String protocolName) {
        WirelessProtocol protocol = supportedProtocols.get(protocolName);
        if (protocol == null) {
            protocol = supportedProtocols.get("WIFI"); // Default to WiFi
        }
        
        double distance = location.distanceTo(device.getCurrentLocation());
        return protocol.calculateActualBandwidth(distance, 0.1) * signalStrength;
    }
    
    /**
     * Simulate node failure
     */
    public void simulateFailure() {
        isHealthy = false;
        System.out.println("Edge node " + nodeId + " has failed!");
        
        // Notify fault tolerance system
        faultTolerance.handleNodeFailure(String.valueOf(nodeId), taskQueue);
    }
    
    /**
     * Recover node from failure
     */
    public void recover() {
        isHealthy = true;
        System.out.println("Edge node " + nodeId + " has recovered");
        
        // Recover tasks from fault tolerance system
        List<Task> recoveredTasks = faultTolerance.recoverTasks(String.valueOf(nodeId));
        for (Task task : recoveredTasks) {
            taskQueue.add(task);
        }
    }
    
    /**
     * Add a new supported wireless protocol
     * @param protocolName Name of the protocol to add
     */
    public void addSupportedProtocol(String protocolName) {
        supportedProtocols.put(protocolName, WirelessProtocolFactory.getProtocol(protocolName));
    }
    
    /**
     * Get analytics results from the data processors
     * @return Map containing analytics results
     */
    public Map<String, Object> getAnalyticsResults() {
        Map<String, Object> results = new HashMap<>();
        
        // Get streaming analytics results
        results.put("streaming", streamingAnalytics.getName());
        
        // Get data aggregation results
        results.put("aggregation", dataAggregator.getName());
        
        return results;
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
    
    public boolean isHealthy() {
        return isHealthy;
    }
    
    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }
    
    public double getTotalEnergyConsumed() {
        return totalEnergyConsumed;
    }
    
    public Map<String, WirelessProtocol> getSupportedProtocols() {
        return supportedProtocols;
    }
    
    /**
     * List of IoT devices connected to this edge node
     * This is a field that should be populated by the EdgeController
     */
    private List<IoTDevice> connectedDevices = new ArrayList<>();
    
    /**
     * Get the list of IoT devices connected to this edge node
     * @return List of connected IoT devices
     */
    public List<IoTDevice> getConnectedDevices() {
        return connectedDevices;
    }
    
    /**
     * Add a connected device to this edge node
     * @param device The IoT device to connect
     */
    public void addConnectedDevice(IoTDevice device) {
        if (!connectedDevices.contains(device)) {
            connectedDevices.add(device);
        }
    }
}
