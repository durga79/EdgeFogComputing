package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import simulation.SimulationArea;
import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import network.WiFiProtocol;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import energy.BatteryModel;

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
    
    // Wireless communication components
    private WirelessProtocol wirelessProtocol;
    private double signalInterference; // 0.0-1.0, higher means more interference
    
    // Energy components
    private EnergyModel energyModel;
    private BatteryModel battery;
    private double totalEnergyConsumed;
    
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
        
        // Initialize with default wireless protocol (WiFi)
        this.wirelessProtocol = WirelessProtocolFactory.getProtocol("WIFI");
        this.signalInterference = 0.1; // Default low interference
        
        // Initialize energy components
        this.energyModel = new LinearEnergyModel();
        this.battery = new BatteryModel(); // Default battery parameters
        this.totalEnergyConsumed = 0.0;
    }
    
    /**
     * Create IoTDevice with specific wireless protocol and battery parameters
     */
    public IoTDevice(int deviceId, String deviceType, double mobilitySpeed, Location initialLocation,
                     String protocolName, double batteryCapacity, double batteryVoltage) {
        this(deviceId, deviceType, mobilitySpeed, initialLocation);
        
        // Set specified wireless protocol
        this.wirelessProtocol = WirelessProtocolFactory.getProtocol(protocolName);
        
        // Create custom battery
        this.battery = new BatteryModel(batteryCapacity, batteryVoltage, 1.0, 0.005, 0.9, 1.0);
    }
    
    /**
     * Create IoTDevice with specific wireless protocol, battery model, and energy model objects
     */
    public IoTDevice(int deviceId, String deviceType, double mobilitySpeed, Location initialLocation,
                     WirelessProtocol protocol, BatteryModel batteryModel, EnergyModel energyModel) {
        this(deviceId, deviceType, mobilitySpeed, initialLocation);
        
        // Set specified wireless protocol
        this.wirelessProtocol = protocol;
        
        // Set specified battery and energy models
        this.battery = batteryModel;
        this.energyModel = energyModel;
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
     * Generate a new task with security parameters
     * @param appType Optional application type (0-3), or -1 for random selection
     * @param securityManager Security manager for token generation
     * @param securityMetadata Additional security metadata
     * @return The generated task
     */
    public Task generateTask(int appType, security.SecurityManager securityManager, Map<String, Object> securityMetadata) {
        // Generate the basic task
        Task task = generateTask(appType);
        
        // Add security token if security manager is provided
        if (securityManager != null) {
            String token = securityManager.generateToken(deviceId);
            task.setSecurityToken(token);
        }
        
        // Add security metadata if provided
        if (securityMetadata != null && !securityMetadata.isEmpty()) {
            for (Map.Entry<String, Object> entry : securityMetadata.entrySet()) {
                task.addMetadata(entry.getKey(), entry.getValue());
            }
        }
        
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
    
    /**
     * Calculate the bandwidth to an edge node based on distance and current wireless protocol
     * 
     * @param edgeNode The edge node to calculate bandwidth to
     * @return The available bandwidth in Kbps
     */
    public double calculateBandwidthToEdge(EdgeNode edgeNode) {
        double distance = currentLocation.distanceTo(edgeNode.getLocation());
        return wirelessProtocol.calculateActualBandwidth(distance, signalInterference);
    }
    
    /**
     * Calculate the latency to an edge node based on distance and current wireless protocol
     * 
     * @param edgeNode The edge node to calculate latency to
     * @param packetSize The size of the packet in bytes
     * @return The latency in milliseconds
     */
    public double calculateLatencyToEdge(EdgeNode edgeNode, double packetSize) {
        double distance = currentLocation.distanceTo(edgeNode.getLocation());
        return wirelessProtocol.calculateActualLatency(distance, packetSize);
    }
    
    /**
     * Simulate sending data to an edge node and calculate energy consumption
     * 
     * @param edgeNode The edge node to send data to
     * @param dataSize The size of data to send in bytes
     * @return True if data was sent successfully, false if battery depleted
     */
    public boolean sendData(EdgeNode edgeNode, long dataSize) {
        double distance = currentLocation.distanceTo(edgeNode.getLocation());
        double bandwidth = wirelessProtocol.calculateActualBandwidth(distance, signalInterference);
        
        // Calculate energy consumption for data transmission
        double transmissionEnergy = energyModel.calculateTransmissionEnergy(
            dataSize,
            bandwidth * 1000 / 8, // Convert Kbps to bytes/sec
            100.0 // 100mW transmission power
        );
        
        // Consume energy from battery
        if (!battery.consumeEnergy(transmissionEnergy)) {
            System.out.println("Device " + deviceId + " failed to send data due to insufficient energy");
            return false;
        }
        
        totalEnergyConsumed += transmissionEnergy;
        return true;
    }
    
    /**
     * Update the device's battery level after idle time
     * 
     * @param idleTimeMs Idle time in milliseconds
     * @return True if battery is still charged, false if depleted
     */
    public boolean updateBatteryAfterIdle(long idleTimeMs) {
        return battery.simulateIdle(idleTimeMs);
    }
    
    /**
     * Change the wireless protocol used by this device
     * 
     * @param protocolName Name of the protocol to use
     */
    public void changeWirelessProtocol(String protocolName) {
        this.wirelessProtocol = WirelessProtocolFactory.getProtocol(protocolName);
        System.out.println("Device " + deviceId + " switched to " + wirelessProtocol.getName() + " protocol");
    }
    
    /**
     * Update the signal interference level (e.g., due to environmental factors)
     * 
     * @param newInterference New interference level (0.0-1.0)
     */
    public void updateSignalInterference(double newInterference) {
        this.signalInterference = Math.max(0.0, Math.min(1.0, newInterference));
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
    
    public WirelessProtocol getWirelessProtocol() {
        return wirelessProtocol;
    }
    
    /**
     * Set the signal interference level for this device
     * @param interference Interference level (0.0-1.0)
     */
    public void setSignalInterference(double interference) {
        this.signalInterference = Math.max(0.0, Math.min(1.0, interference));
    }
    
    /**
     * Update battery level for idle time
     * @param seconds Seconds of idle time
     */
    public void updateBatteryForIdleTime(int seconds) {
        // Calculate idle power consumption (much lower than active)
        double idlePowerWatts = 0.05; // 50 milliwatts when idle
        double energyConsumed = idlePowerWatts * seconds;
        
        // Update battery and total energy consumed
        battery.discharge(energyConsumed);
        totalEnergyConsumed += energyConsumed;
    }
    
    public double getBatteryLevel() {
        return battery.getChargeLevel();
    }
    
    public double getTotalEnergyConsumed() {
        return totalEnergyConsumed;
    }
    
    public void chargeBattery(double amount) {
        battery.charge(amount);
    }
    
    public void fullyChargeBattery() {
        battery.fullyCharge();
    }
}
