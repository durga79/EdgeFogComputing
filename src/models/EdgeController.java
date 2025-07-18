package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fuzzy_logic.FuzzyLogicController;

/**
 * Edge Controller that manages task offloading decisions
 * This is a key component in the system architecture as described in the research paper
 */
public class EdgeController {
    private List<EdgeNode> edgeNodes;
    private Cloud cloud;
    private FuzzyLogicController fuzzyLogicController;
    private Map<Integer, EdgeNode> deviceToEdgeMap; // Maps devices to their connected edge nodes
    
    public EdgeController(List<EdgeNode> edgeNodes, Cloud cloud) {
        this.edgeNodes = edgeNodes;
        this.cloud = cloud;
        this.fuzzyLogicController = new FuzzyLogicController();
        this.deviceToEdgeMap = new HashMap<>();
    }
    
    /**
     * Register an IoT device with its nearest edge node
     * @param device The IoT device to register
     */
    public void registerDevice(IoTDevice device) {
        EdgeNode nearestEdge = device.findNearestEdgeNode(edgeNodes);
        deviceToEdgeMap.put(device.getDeviceId(), nearestEdge);
    }
    
    /**
     * Make offloading decision for a task using fuzzy logic
     * @param task The task to be offloaded
     * @return The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     */
    public String makeOffloadingDecision(Task task) {
        // Get the connected edge node for the device
        EdgeNode connectedEdge = deviceToEdgeMap.get(task.getSourceDeviceId());
        
        if (connectedEdge == null) {
            // If no edge node is connected, default to cloud
            return "CLOUD";
        }
        
        // Get task and edge node parameters for fuzzy logic
        double cpuDemand = task.getCpuDemand();
        double networkDemand = task.getNetworkDemand();
        double delaySensitivity = task.getDelaySensitivity();
        double edgeUtilization = connectedEdge.getCpuUtilization();
        int resourceType = connectedEdge.getResourceType();
        
        // Use fuzzy logic controller to make decision
        String decision = fuzzyLogicController.makeOffloadingDecision(
            cpuDemand, networkDemand, delaySensitivity, edgeUtilization, resourceType);
        
        return decision;
    }
    
    /**
     * Process a task based on the offloading decision
     * @param task The task to be processed
     * @param offloadingDecision The offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     */
    public void processTask(Task task, String offloadingDecision) {
        EdgeNode connectedEdge = deviceToEdgeMap.get(task.getSourceDeviceId());
        
        switch (offloadingDecision) {
            case "LOCAL_EDGE":
                // Process on the connected edge node
                task.setExecutionLocation("LOCAL_EDGE");
                connectedEdge.queueTask(task);
                break;
                
            case "OTHER_EDGE":
                // Find another edge node with lower utilization
                EdgeNode otherEdge = findLeastLoadedEdge(connectedEdge);
                task.setExecutionLocation("OTHER_EDGE");
                otherEdge.queueTask(task);
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
    public Map<String, Double> getSystemStatistics() {
        Map<String, Double> stats = new HashMap<>();
        
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
        
        return stats;
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
}
