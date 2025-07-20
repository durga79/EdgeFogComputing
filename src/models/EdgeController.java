package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fuzzy_logic.FuzzyLogicController;
import network.WirelessProtocol;
import security.SecurityManager;
import security.SecureCommunication;
import security.SecureOffloading;
import services.ServiceDiscovery;
import services.ServiceMigration;
import services.FaultTolerance;

/**
 * Edge Controller that manages task offloading decisions
 * This is a key component in the system architecture as described in the research paper
 */
public class EdgeController {
    private List<EdgeNode> edgeNodes;
    private Cloud cloud;
    private FuzzyLogicController fuzzyLogicController;
    private Map<Integer, EdgeNode> deviceToEdgeMap; // Maps devices to their connected edge nodes
    
    // Security components
    private SecurityManager securityManager;
    private SecureCommunication secureCommunication;
    private SecureOffloading secureOffloading;
    
    // Service management components
    private ServiceDiscovery globalServiceDiscovery;
    private ServiceMigration serviceMigration;
    private FaultTolerance globalFaultTolerance;
    
    // System statistics
    private double totalSystemEnergyConsumed;
    private int securityIncidentsDetected;
    private int successfulMigrations;
    private int failedMigrations;
    private int recoveredTasks;
    
    public EdgeController(List<EdgeNode> edgeNodes, Cloud cloud) {
        this.edgeNodes = edgeNodes;
        this.cloud = cloud;
        this.fuzzyLogicController = new FuzzyLogicController();
        this.deviceToEdgeMap = new HashMap<>();
        
        // Initialize security components
        this.securityManager = new SecurityManager();
        this.secureCommunication = new SecureCommunication();
        this.secureOffloading = new SecureOffloading(securityManager, secureCommunication);
        
        // Initialize service management components
        this.globalServiceDiscovery = new ServiceDiscovery();
        this.serviceMigration = new ServiceMigration();
        this.globalFaultTolerance = new FaultTolerance();
        
        // Initialize statistics
        this.totalSystemEnergyConsumed = 0.0;
        this.securityIncidentsDetected = 0;
        this.successfulMigrations = 0;
        this.failedMigrations = 0;
        this.recoveredTasks = 0;
        
        // Register system services
        registerSystemServices();
    }
    
    /**
     * Register system-wide services
     */
    private void registerSystemServices() {
        // Register cloud services
        Map<String, Object> cloudMetadata = new HashMap<>();
        cloudMetadata.put("mips", cloud.getMips());
        cloudMetadata.put("unlimited", true);
        
        globalServiceDiscovery.registerService(
            "cloud_computation", 
            "High-performance cloud computation service",
            "CLOUD", // Service type
            null, // Cloud is not an edge node
            cloudMetadata
        );
        
        // Register edge controller services
        Map<String, Object> controllerMetadata = new HashMap<>();
        controllerMetadata.put("edgeNodes", edgeNodes.size());
        
        globalServiceDiscovery.registerService(
            "task_offloading", 
            "Task offloading decision service",
            "CONTROLLER", // Service type
            null, // Controller is not an edge node
            controllerMetadata
        );
    }
    
    /**
     * Register an IoT device with its nearest edge node
     * @param device The IoT device to register
     */
    public void registerDevice(IoTDevice device) {
        EdgeNode nearestEdge = device.findNearestEdgeNode(edgeNodes);
        deviceToEdgeMap.put(device.getDeviceId(), nearestEdge);
        
        // Register device with security manager
        securityManager.registerUser("device-" + device.getDeviceId(), 
                                    "device", 
                                    security.SecurityManager.UserRole.GUEST);
    }
    
    /**
     * Make offloading decision for a task using fuzzy logic and additional factors
     * @param task The task to be offloaded
     * @return The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     */
    public String makeOffloadingDecision(Task task) {
        // Get the device that generated this task
        int sourceDeviceId = task.getSourceDeviceId();
        IoTDevice device = null;
        
        // Find the device in the edge nodes' connected devices
        for (EdgeNode edge : edgeNodes) {
            for (IoTDevice connectedDevice : edge.getConnectedDevices()) {
                if (connectedDevice.getDeviceId() == sourceDeviceId) {
                    device = connectedDevice;
                    break;
                }
            }
            if (device != null) break;
        }
        
        // If device not found, use default decision
        if (device == null) {
            return "CLOUD"; // Default to cloud if device not found
        }
        
        return makeOffloadingDecision(task, device);
    }
    
    /**
     * Make offloading decision for a task using fuzzy logic and additional factors
     * @param task The task to be offloaded
     * @param device The source IoT device
     * @return The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     */
    public String makeOffloadingDecision(Task task, IoTDevice device) {
        // Get the connected edge node for the device
        EdgeNode connectedEdge = deviceToEdgeMap.get(task.getSourceDeviceId());
        
        if (connectedEdge == null) {
            // If no edge node is connected, default to cloud
            return "CLOUD";
        }
        
        // Check if the edge node is healthy
        if (!connectedEdge.isHealthy()) {
            System.out.println("Edge node " + connectedEdge.getNodeId() + " is unhealthy, offloading to cloud");
            return "CLOUD";
        }
        
        // Get task and edge node parameters for fuzzy logic
        double cpuDemand = task.getCpuDemand();
        double networkDemand = task.getNetworkDemand();
        double delaySensitivity = task.getDelaySensitivity();
        double edgeUtilization = connectedEdge.getCpuUtilization();
        int resourceType = connectedEdge.getResourceType();
        
        // Get wireless network conditions
        double bandwidth = device.calculateBandwidthToEdge(connectedEdge);
        double batteryLevel = device.getBatteryLevel();
        
        // Adjust decision based on battery level - if low battery, prefer offloading
        boolean lowBattery = batteryLevel < 0.2;
        
        // Adjust decision based on bandwidth - if low bandwidth, avoid OTHER_EDGE
        boolean lowBandwidth = bandwidth < 1000; // Less than 1 Mbps
        
        // Use fuzzy logic controller to make initial decision
        String decision = fuzzyLogicController.makeOffloadingDecision(
            cpuDemand, networkDemand, delaySensitivity, edgeUtilization, resourceType);
        
        // Adjust decision based on additional factors
        if (lowBattery && decision.equals("LOCAL_EDGE")) {
            // If battery is low, prefer offloading to save energy
            decision = "OTHER_EDGE";
        }
        
        if (lowBandwidth && decision.equals("OTHER_EDGE")) {
            // If bandwidth is low, avoid transferring to other edge
            decision = "CLOUD";
        }
        
        // If task has security requirements, ensure appropriate execution location
        if (task.hasSecurityMetadata()) {
            String securityLevel = (String) task.getSecurityMetadata().get("securityLevel");
            if ("high".equals(securityLevel) && !decision.equals("LOCAL_EDGE")) {
                // For high security tasks, prefer local edge or cloud
                decision = "CLOUD"; // Cloud assumed to have better security than random edge node
            }
        }
        
        return decision;
    }
    
    /**
     * Process a task based on the offloading decision with security and energy considerations
     * @param task The task to be processed
     * @param offloadingDecision The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     * @return True if task was successfully offloaded, false otherwise
     */
    public boolean processTask(Task task, String offloadingDecision) {
        // Get the device that generated this task
        int sourceDeviceId = task.getSourceDeviceId();
        IoTDevice device = null;
        
        // Find the device in the edge nodes' connected devices
        for (EdgeNode edge : edgeNodes) {
            for (IoTDevice connectedDevice : edge.getConnectedDevices()) {
                if (connectedDevice.getDeviceId() == sourceDeviceId) {
                    device = connectedDevice;
                    break;
                }
            }
            if (device != null) break;
        }
        
        // If device not found, use default processing
        if (device == null) {
            // Process on cloud as fallback
            task.setExecutionLocation("CLOUD");
            cloud.queueTask(task);
            return true;
        }
        
        return processTask(task, device, offloadingDecision);
    }
    
    /**
     * Process a task based on the offloading decision with security and energy considerations
     * @param task The task to be processed
     * @param device The source IoT device
     * @param offloadingDecision The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     * @return True if task was successfully offloaded, false otherwise
     */
    public boolean processTask(Task task, IoTDevice device, String offloadingDecision) {
        EdgeNode connectedEdge = deviceToEdgeMap.get(task.getSourceDeviceId());
        
        // Apply security measures if needed
        if (task.hasSecurityMetadata()) {
            // Encrypt the task for secure transmission
            task.encrypt(secureCommunication);
        }
        
        // Simulate data transmission energy consumption
        boolean transmissionSuccess = true;
        if (!offloadingDecision.equals("LOCAL_EDGE")) {
            // Calculate data size to transmit (task + overhead)
            long dataSize = (long) (task.getNetworkDemand() * 1024); // Convert KB to bytes
            transmissionSuccess = device.sendData(connectedEdge, dataSize);
            
            if (!transmissionSuccess) {
                System.out.println("Task transmission failed due to energy constraints");
                return false;
            }
        }
        
        // Process based on offloading decision
        switch (offloadingDecision) {
            case "LOCAL_EDGE":
                // Process on the connected edge node
                task.setExecutionLocation("LOCAL_EDGE");
                connectedEdge.queueTask(task);
                break;
                
            case "OTHER_EDGE":
                // Find another edge node with lower utilization
                EdgeNode otherEdge = findLeastLoadedEdge(connectedEdge);
                
                // Create a migration session
                boolean migrationSuccess = serviceMigration.startMigration(
                    String.valueOf(connectedEdge.getNodeId()), 
                    String.valueOf(otherEdge.getNodeId()),
                    "COMPUTATION",
                    task.getTaskId()
                ) != null;
                
                if (migrationSuccess) {
                    task.setExecutionLocation("OTHER_EDGE");
                    otherEdge.queueTask(task);
                    successfulMigrations++;
                } else {
                    // Migration failed, use cloud as fallback
                    task.setExecutionLocation("CLOUD");
                    cloud.queueTask(task);
                    failedMigrations++;
                }
                break;
                
            case "CLOUD":
                // Process on the cloud
                task.setExecutionLocation("CLOUD");
                cloud.queueTask(task);
                break;
                
            default:
                // Default to connected edge
                task.setExecutionLocation("LOCAL_EDGE");
                connectedEdge.queueTask(task);
        }
        
        return true;
    }
    
    /**
     * Find the edge node with the lowest CPU utilization (excluding the current one)
     * @param currentEdge The current edge node to exclude
     * @return The edge node with lowest utilization
     */
    private EdgeNode findLeastLoadedEdge(EdgeNode currentEdge) {
        EdgeNode leastLoaded = currentEdge; // Default to current edge
        double minUtilization = Double.MAX_VALUE;
        
        for (EdgeNode edge : edgeNodes) {
            if (edge.getNodeId() != currentEdge.getNodeId() && 
                edge.getCpuUtilization() < minUtilization) {
                minUtilization = edge.getCpuUtilization();
                leastLoaded = edge;
            }
        }
        
        return leastLoaded;
    }
    
    /**
     * Get statistics about task processing across the system
     * @return A map of statistics
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate average service time for each location type
        double localEdgeServiceTime = calculateAverageServiceTime("LOCAL_EDGE");
        double otherEdgeServiceTime = calculateAverageServiceTime("OTHER_EDGE");
        double cloudServiceTime = calculateAverageServiceTime("CLOUD");
        
        stats.put("localEdgeServiceTime", localEdgeServiceTime);
        stats.put("otherEdgeServiceTime", otherEdgeServiceTime);
        stats.put("cloudServiceTime", cloudServiceTime);
        
        // Calculate average CPU utilization across edge nodes
        double avgEdgeUtilization = 0.0;
        for (EdgeNode edge : edgeNodes) {
            avgEdgeUtilization += edge.getCpuUtilization();
        }
        avgEdgeUtilization /= edgeNodes.size();
        stats.put("averageEdgeUtilization", avgEdgeUtilization);
        
        // Cloud utilization
        stats.put("cloudUtilization", cloud.getCpuUtilization());
        
        // Energy statistics
        double totalEdgeEnergy = 0.0;
        for (EdgeNode edge : edgeNodes) {
            totalEdgeEnergy += edge.getTotalEnergyConsumed();
        }
        stats.put("totalEdgeEnergyConsumed", totalEdgeEnergy);
        stats.put("totalSystemEnergyConsumed", totalSystemEnergyConsumed);
        
        // Security statistics
        stats.put("securityIncidentsDetected", securityIncidentsDetected);
        
        // Service management statistics
        stats.put("successfulMigrations", successfulMigrations);
        stats.put("failedMigrations", failedMigrations);
        stats.put("recoveredTasks", recoveredTasks);
        stats.put("registeredServices", globalServiceDiscovery.getServiceCount());
        
        return stats;
    }
    
    /**
     * Simulate a node failure and recovery
     * @param nodeId The ID of the node to fail
     * @param recoverAfterMs Time in milliseconds after which the node should recover
     */
    public void simulateNodeFailure(int nodeId, long recoverAfterMs) {
        // Find the node
        EdgeNode targetNode = null;
        for (EdgeNode node : edgeNodes) {
            if (node.getNodeId() == nodeId) {
                targetNode = node;
                break;
            }
        }
        
        if (targetNode == null) {
            System.out.println("Node with ID " + nodeId + " not found");
            return;
        }
        
        // Simulate failure
        targetNode.simulateFailure();
        
        // Schedule recovery in a separate thread
        final EdgeNode finalTargetNode = targetNode;
        new Thread(() -> {
            try {
                Thread.sleep(recoverAfterMs);
                finalTargetNode.recover();
                // Use atomic operation or synchronize for thread safety
                synchronized(this) {
                    recoveredTasks += finalTargetNode.getQueueSize();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Find services of a specific type across all edge nodes
     * @param serviceType The type of service to find
     * @return List of service information maps
     */
    public List<ServiceDiscovery.ServiceRegistration> findServices(String serviceType) {
        return globalServiceDiscovery.findServicesByType(serviceType);
    }
    
    /**
     * Update system energy consumption with device energy
     * @param device The IoT device to get energy consumption from
     */
    public void updateEnergyStatistics(IoTDevice device) {
        totalSystemEnergyConsumed += device.getTotalEnergyConsumed();
    }
    
    /**
     * Report a security incident
     * @param incidentType Type of security incident
     * @param details Details about the incident
     */
    public void reportSecurityIncident(String incidentType, String details) {
        securityIncidentsDetected++;
        System.out.println("Security incident detected: " + incidentType + " - " + details);
    }
    
    /**
     * Calculate average service time for tasks processed at a specific location
     * @param location The execution location (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     * @return Average service time in milliseconds
     */
    private double calculateAverageServiceTime(String location) {
        List<Task> tasks = new ArrayList<>();
        
        // Collect tasks from edge nodes
        for (EdgeNode edge : edgeNodes) {
            for (Task task : edge.getCompletedTasks()) {
                if (task.getExecutionLocation().equals(location)) {
                    tasks.add(task);
                }
            }
        }
        
        // Collect tasks from cloud
        if (location.equals("CLOUD")) {
            tasks.addAll(cloud.getCompletedTasks());
        }
        
        // Calculate average service time
        if (tasks.isEmpty()) {
            return 0.0;
        }
        
        long totalServiceTime = 0;
        for (Task task : tasks) {
            totalServiceTime += task.getActualServiceTime();
        }
        
        return (double) totalServiceTime / tasks.size();
    }
    
    // Getters
    public List<EdgeNode> getEdgeNodes() {
        return edgeNodes;
    }
    
    public Cloud getCloud() {
        return cloud;
    }
    
    public SecurityManager getSecurityManager() {
        return securityManager;
    }
    
    public SecureCommunication getSecureCommunication() {
        return secureCommunication;
    }
    
    public ServiceDiscovery getGlobalServiceDiscovery() {
        return globalServiceDiscovery;
    }
    
    public ServiceMigration getServiceMigration() {
        return serviceMigration;
    }
    
    public FaultTolerance getGlobalFaultTolerance() {
        return globalFaultTolerance;
    }
}
