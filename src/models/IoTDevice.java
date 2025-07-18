package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import simulation.SimulationArea;

/**
 * Represents an IoT device that generates tasks for offloading
 */
public class IoTDevice {
    private int deviceId;
    private String deviceType;
    private double mobilitySpeed;
    private Location currentLocation;
    private List<Task> generatedTasks;
    private Random random;
    
    // Application profiles (CPU demand, network demand, delay sensitivity)
    private static final double[][] APP_PROFILES = {
        {3000, 1500, 0.9},  // App Type 1: Low CPU, Low Network, High Delay Sensitivity
        {6000, 2500, 0.7},  // App Type 2: Medium CPU, Medium Network, Medium Delay Sensitivity
        {10000, 3500, 0.5}, // App Type 3: High CPU, Medium Network, Medium Delay Sensitivity
        {15000, 5000, 0.1}  // App Type 4: Very High CPU, High Network, Low Delay Sensitivity
    };
    
    public IoTDevice(int deviceId, String deviceType, double mobilitySpeed, Location initialLocation) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.mobilitySpeed = mobilitySpeed;
        this.currentLocation = initialLocation;
        this.generatedTasks = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Generate a new task based on application profiles
     * @param appType Optional application type (0-3), or -1 for random selection
     * @return The generated task
     */
    public Task generateTask(int appType) {
        // If appType is -1 or invalid, randomly select an application profile
        if (appType < 0 || appType >= APP_PROFILES.length) {
            appType = random.nextInt(APP_PROFILES.length);
        }
        
        // Get application characteristics
        double cpuDemand = APP_PROFILES[appType][0];
        double networkDemand = APP_PROFILES[appType][1];
        double delaySensitivity = APP_PROFILES[appType][2];
        
        // Add some randomness to task parameters
        cpuDemand = cpuDemand * (0.9 + 0.2 * random.nextDouble());
        networkDemand = networkDemand * (0.9 + 0.2 * random.nextDouble());
        
        // Create and return the task
        Task task = new Task(
            deviceId + "-" + generatedTasks.size(),
            deviceId,
            cpuDemand,
            networkDemand,
            delaySensitivity,
            System.currentTimeMillis()
        );
        
        generatedTasks.add(task);
        return task;
    }
    
    /**
     * Generate a new task with random application type
     * @return The generated task
     */
    public Task generateTask() {
        return generateTask(-1); // Use random app type
    }
    
    /**
     * Update device location based on mobility model
     * @param simulationArea The area in which the device can move
     */
    public void updateLocation(simulation.SimulationArea simulationArea) {
        // Simple random walk mobility model
        double newX = currentLocation.getX() + (random.nextDouble() - 0.5) * mobilitySpeed;
        double newY = currentLocation.getY() + (random.nextDouble() - 0.5) * mobilitySpeed;
        
        // Ensure the device stays within the simulation area
        newX = Math.max(0, Math.min(newX, simulationArea.getWidth()));
        newY = Math.max(0, Math.min(newY, simulationArea.getHeight()));
        
        // Update the current location
        currentLocation = new Location(newX, newY);
    }
    
    /**
     * Find the nearest edge node to this device
     * @param edgeNodes List of available edge nodes
     * @return The nearest edge node or null if no edge nodes are available
     */
    public EdgeNode findNearestEdgeNode(List<EdgeNode> edgeNodes) {
        if (edgeNodes == null || edgeNodes.isEmpty()) {
            System.out.println("Warning: No edge nodes available for device " + deviceId);
            return null;
        }
        
        EdgeNode nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (EdgeNode edgeNode : edgeNodes) {
            double distance = currentLocation.distanceTo(edgeNode.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = edgeNode;
            }
        }
        
        if (nearest != null) {
            System.out.println("Device " + deviceId + " connected to Edge Node " + nearest.getNodeId() + 
                             " at distance " + String.format("%.2f", minDistance));
        }

        
        
        return nearest;
    }
    
    // Getters and setters
    public int getDeviceId() {
        return deviceId;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public Location getCurrentLocation() {
        return currentLocation;
    }
    
    public List<Task> getGeneratedTasks() {
        return generatedTasks;
    }
}
