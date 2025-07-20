package simulation;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import models.Cloud;
import models.EdgeController;
import models.EdgeNode;
import models.IoTDevice;
import models.Location;
import models.Task;
import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import energy.BatteryModel;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import security.SecurityManager;
import security.SecureCommunication;
import security.SecureOffloading;
import services.ServiceDiscovery;
import services.ServiceMigration;
import services.FaultTolerance;
import services.ServiceDiscovery.ServiceRegistration;
import bigdata.DataAggregator;
import bigdata.StreamingAnalytics;

/**
 * Main simulation class for the Edge-Fog computing task offloading system
 */
public class EdgeFogSimulation {
    // Simulation parameters
    private int simulationTime;
    private int warmUpPeriod;
    private double vmLoadCheckInterval;
    private double locationCheckInterval;
    private boolean fileLogEnabled;
    private boolean advancedFeaturesEnabled; // Flag to enable/disable advanced features
    
    // IoT device parameters
    private int minNumberOfMobileDevices;
    private int maxNumberOfMobileDevices;
    private int mobileDeviceCounterSize;
    private String mobilityModel;
    private double speedOfMobileDevice;
    
    // Network parameters
    private double wlanBandwidth;
    private double wanBandwidth;
    private double wanPropagationDelay;
    private double lanInternalDelay;
    private double wlanPropagationDelay;
    
    // Wireless protocol parameters
    private String defaultWirelessProtocol;
    private Map<String, Double> protocolInterferenceFactors;
    private double signalFadingFactor;
    
    // Energy parameters
    private double batteryCapacity;
    private double energyConsumptionRate;
    private double idleEnergyConsumption;
    private double computationEnergyFactor;
    private double transmissionEnergyFactor;
    
    // Security parameters
    private boolean securityEnabled;
    private String encryptionAlgorithm;
    private int securityLevel;
    
    // Service management parameters
    private int serviceDiscoveryInterval;
    private double faultToleranceProbability;
    private int checkpointInterval;
    
    // Edge server parameters
    private int numOfEdgeDatacenters;
    private int numOfEdgeHosts;
    private int numOfEdgeVms;
    private double edgeVmMips;
    private int edgeVmRam;
    private int edgeVmStorage;
    
    // Cloud parameters
    private double cloudVmMips;
    private int cloudVmRam;
    private int cloudVmStorage;
    
    // Application parameters
    private int numOfAppTypes;
    private double[] usagePercentage;
    private double[] probCloudSelection;
    private double[] poissonInterarrival;
    private double[] delaySensitivity;
    private double[] taskLength;
    private double[] taskInputSize;
    private double[] taskOutputSize;
    
    // Orchestrator policy
    private String orchestratorPolicy;
    
    // Advanced analytics parameters
    private int analyticsWindowSize;
    private int dataAggregationInterval;
    private boolean realTimeAnalyticsEnabled;
    
    // Simulation components
    private List<EdgeNode> edgeNodes;
    private Cloud cloud;
    private List<IoTDevice> iotDevices;
    private EdgeController edgeController;
    private Random random;
    
    // Results collection
    private List<Map<String, Object>> statisticsHistory;
    
    // Protocol factory
    private WirelessProtocolFactory protocolFactory;
    
    public EdgeFogSimulation(String configFile) {
        loadConfiguration(configFile);
        random = new Random();
        statisticsHistory = new ArrayList<>();
        
        // Initialize protocol factory
        protocolFactory = new WirelessProtocolFactory();
        
        // Initialize protocol interference factors
        protocolInterferenceFactors = new HashMap<>();
        protocolInterferenceFactors.put("WIFI", 0.1);
        protocolInterferenceFactors.put("LORAWAN", 0.05);
        protocolInterferenceFactors.put("NBIOT", 0.08);
        protocolInterferenceFactors.put("5G", 0.03);
    }
    
    /**
     * Load configuration from properties file
     * @param configFile Path to configuration file
     */
    private void loadConfiguration(String configFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(configFile));
            
            // Simulation parameters
            simulationTime = Integer.parseInt(props.getProperty("simulation_time", "30"));
            warmUpPeriod = Integer.parseInt(props.getProperty("warm_up_period", "3"));
            vmLoadCheckInterval = Double.parseDouble(props.getProperty("vm_load_check_interval", "0.1"));
            locationCheckInterval = Double.parseDouble(props.getProperty("location_check_interval", "0.1"));
            fileLogEnabled = Boolean.parseBoolean(props.getProperty("file_log_enabled", "true"));
            advancedFeaturesEnabled = Boolean.parseBoolean(props.getProperty("advanced_features_enabled", "true"));
            
            // IoT device parameters
            minNumberOfMobileDevices = Integer.parseInt(props.getProperty("min_number_of_mobile_devices", "200"));
            maxNumberOfMobileDevices = Integer.parseInt(props.getProperty("max_number_of_mobile_devices", "2000"));
            mobileDeviceCounterSize = Integer.parseInt(props.getProperty("mobile_device_counter_size", "200"));
            mobilityModel = props.getProperty("mobility_model", "RANDOM_WALK");
            speedOfMobileDevice = Double.parseDouble(props.getProperty("speed_of_mobile_device", "1.0"));
            
            // Network parameters
            wlanBandwidth = Double.parseDouble(props.getProperty("wlan_bandwidth", "100"));
            wanBandwidth = Double.parseDouble(props.getProperty("wan_bandwidth", "10"));
            wanPropagationDelay = Double.parseDouble(props.getProperty("wan_propagation_delay", "0.15"));
            lanInternalDelay = Double.parseDouble(props.getProperty("lan_internal_delay", "0.005"));
            wlanPropagationDelay = Double.parseDouble(props.getProperty("wlan_propagation_delay", "0.02"));
            
            // Wireless protocol parameters
            defaultWirelessProtocol = props.getProperty("default_wireless_protocol", "WiFi");
            signalFadingFactor = Double.parseDouble(props.getProperty("signal_fading_factor", "0.8"));
            
            // Energy parameters
            batteryCapacity = Double.parseDouble(props.getProperty("battery_capacity", "5000"));
            energyConsumptionRate = Double.parseDouble(props.getProperty("energy_consumption_rate", "1.0"));
            idleEnergyConsumption = Double.parseDouble(props.getProperty("idle_energy_consumption", "0.1"));
            computationEnergyFactor = Double.parseDouble(props.getProperty("computation_energy_factor", "0.5"));
            transmissionEnergyFactor = Double.parseDouble(props.getProperty("transmission_energy_factor", "0.8"));
            
            // Security parameters
            securityEnabled = Boolean.parseBoolean(props.getProperty("security_enabled", "true"));
            encryptionAlgorithm = props.getProperty("encryption_algorithm", "AES");
            securityLevel = Integer.parseInt(props.getProperty("security_level", "2"));
            
            // Service management parameters
            serviceDiscoveryInterval = Integer.parseInt(props.getProperty("service_discovery_interval", "5"));
            faultToleranceProbability = Double.parseDouble(props.getProperty("fault_tolerance_probability", "0.05"));
            checkpointInterval = Integer.parseInt(props.getProperty("checkpoint_interval", "10"));
            
            // Advanced analytics parameters
            analyticsWindowSize = Integer.parseInt(props.getProperty("analytics_window_size", "10"));
            dataAggregationInterval = Integer.parseInt(props.getProperty("data_aggregation_interval", "5"));
            realTimeAnalyticsEnabled = Boolean.parseBoolean(props.getProperty("real_time_analytics_enabled", "true"));
            
            // Edge server parameters
            numOfEdgeDatacenters = Integer.parseInt(props.getProperty("num_of_edge_datacenters", "3"));
            numOfEdgeHosts = Integer.parseInt(props.getProperty("num_of_edge_hosts", "1"));
            numOfEdgeVms = Integer.parseInt(props.getProperty("num_of_edge_vms", "2"));
            edgeVmMips = Double.parseDouble(props.getProperty("edge_vm_mips", "10000"));
            edgeVmRam = Integer.parseInt(props.getProperty("edge_vm_ram", "4000"));
            edgeVmStorage = Integer.parseInt(props.getProperty("edge_vm_storage", "10000"));
            
            // Cloud parameters
            cloudVmMips = Double.parseDouble(props.getProperty("cloud_vm_mips", "20000"));
            cloudVmRam = Integer.parseInt(props.getProperty("cloud_vm_ram", "16000"));
            cloudVmStorage = Integer.parseInt(props.getProperty("cloud_vm_storage", "100000"));
            
            // Application parameters
            numOfAppTypes = Integer.parseInt(props.getProperty("num_of_app_types", "4"));
            
            // Parse comma-separated values
            usagePercentage = parseDoubleArray(props.getProperty("usage_percentage", "25,25,25,25"));
            probCloudSelection = parseDoubleArray(props.getProperty("prob_cloud_selection", "0.1,0.2,0.5,0.8"));
            poissonInterarrival = parseDoubleArray(props.getProperty("poisson_interarrival", "5,10,15,20"));
            delaySensitivity = parseDoubleArray(props.getProperty("delay_sensitivity", "0.9,0.7,0.5,0.1"));
            taskLength = parseDoubleArray(props.getProperty("task_length", "3000,6000,10000,15000"));
            taskInputSize = parseDoubleArray(props.getProperty("task_input_size", "1500,2500,3500,5000"));
            taskOutputSize = parseDoubleArray(props.getProperty("task_output_size", "50,100,150,300"));
            
            // Orchestrator policy
            orchestratorPolicy = props.getProperty("orchestrator_policy", "FUZZY_LOGIC");
            
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parse comma-separated double values
     * @param str String of comma-separated values
     * @return Array of double values
     */
    private double[] parseDoubleArray(String str) {
        String[] parts = str.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }
    
    /**
     * Initialize the simulation environment
     */
    public void initialize() {
        // Create edge nodes
        edgeNodes = new ArrayList<>();
        for (int i = 0; i < numOfEdgeDatacenters; i++) {
            // Create edge nodes with different resource types
            int resourceType = (i % 2) + 1; // Alternating between type 1 and 2
            double mips = resourceType == 1 ? edgeVmMips : edgeVmMips * 1.5;
            int ram = resourceType == 1 ? edgeVmRam : edgeVmRam * 2;
            
            // Place edge nodes in different locations
            double x = 100 * (i + 1);
            double y = 100 * (i + 1);
            Location location = new Location(x, y);
            
            // Create energy model for edge node
            EnergyModel edgeEnergyModel = new LinearEnergyModel(computationEnergyFactor);
            
            // Create edge node with advanced features if enabled
            EdgeNode edgeNode;
            if (advancedFeaturesEnabled) {
                // Create with advanced features
                edgeNode = new EdgeNode(
                    i,
                    "Edge-" + i,
                    location,
                    mips,
                    ram,
                    edgeVmStorage,
                    resourceType
                );
                
                // Set energy model if needed
                // Note: Using existing energy model initialization in EdgeNode constructor
                
                // Add supported wireless protocols
                edgeNode.addSupportedProtocol("WiFi");
                
                // Add additional protocols based on node type
                if (resourceType == 2) { // More powerful nodes support more protocols
                    edgeNode.addSupportedProtocol("5G");
                    edgeNode.addSupportedProtocol("NB-IoT");
                } else {
                    edgeNode.addSupportedProtocol("LoRaWAN");
                }
                
                // Service discovery and fault tolerance are already initialized in the EdgeNode constructor
                
                // Big data analytics components are already initialized in the EdgeNode constructor
                // with default window size and aggregation interval
            } else {
                // Create with basic features
                edgeNode = new EdgeNode(
                    i,
                    "Edge-" + i,
                    location,
                    mips,
                    ram,
                    edgeVmStorage,
                    resourceType
                );
            }
            
            edgeNodes.add(edgeNode);
        }
        
        // Create cloud
        cloud = new Cloud(
            "Cloud-1",
            cloudVmMips,
            cloudVmRam,
            cloudVmStorage,
            wanPropagationDelay,
            wanBandwidth
        );
        
        // Create edge controller
        edgeController = new EdgeController(edgeNodes, cloud);
        
        // Create IoT devices
        iotDevices = new ArrayList<>();
        for (int i = 0; i < minNumberOfMobileDevices; i++) {
            // Random location within simulation area
            double x = random.nextDouble() * 500;
            double y = random.nextDouble() * 500;
            Location location = new Location(x, y);
            
            // Create device with random type
            String deviceType = "Device-Type-" + (random.nextInt(4) + 1);
            
            // Create IoT device with advanced features if enabled
            IoTDevice device;
            if (advancedFeaturesEnabled) {
                // Create wireless protocol for device
                WirelessProtocol protocol;
                
                // Assign different protocols based on device type
                int deviceTypeNum = Integer.parseInt(deviceType.substring(deviceType.length() - 1));
                switch (deviceTypeNum) {
                    case 1:
                        protocol = WirelessProtocolFactory.getProtocol("WIFI");
                        break;
                    case 2:
                        protocol = WirelessProtocolFactory.getProtocol("LORAWAN");
                        break;
                    case 3:
                        protocol = WirelessProtocolFactory.getProtocol("NBIOT");
                        break;
                    case 4:
                        protocol = WirelessProtocolFactory.getProtocol("5G");
                        break;
                    default:
                        protocol = WirelessProtocolFactory.getProtocol(defaultWirelessProtocol);
                }
                
                // Create battery model
                BatteryModel batteryModel = new BatteryModel(batteryCapacity);
                
                // Create energy model
                EnergyModel energyModel = new LinearEnergyModel(energyConsumptionRate);
                
                // Create device with advanced features
                device = new IoTDevice(
                    i,
                    deviceType,
                    speedOfMobileDevice,
                    location,
                    protocol,
                    batteryModel,
                    energyModel
                );
                
                // Set signal interference based on protocol
                try {
                    String protocolType = "WIFI"; // Default protocol type
                    if (protocol != null && protocol.getType() != null) {
                        protocolType = protocol.getType();
                    }
                    double interference = protocolInterferenceFactors.getOrDefault(protocolType, 0.1);
                    device.setSignalInterference(interference * (0.8 + 0.4 * random.nextDouble())); // Add some randomness
                } catch (Exception e) {
                    // Fallback to default interference if any exception occurs
                    device.setSignalInterference(0.1 * (0.8 + 0.4 * random.nextDouble()));
                    System.err.println("Warning: Using default interference for device " + i + ": " + e.getMessage());
                }
            } else {
                // Create with basic features
                device = new IoTDevice(
                    i,
                    deviceType,
                    speedOfMobileDevice,
                    location
                );
            }
            
            // Register device with edge controller
            edgeController.registerDevice(device);
            
            iotDevices.add(device);
        }
        
        // Simulate random node failures if fault tolerance is enabled
        if (advancedFeaturesEnabled && faultToleranceProbability > 0) {
            scheduleRandomNodeFailures();
        }
    }
    
    /**
     * Schedule random node failures during simulation
     */
    private void scheduleRandomNodeFailures() {
        new Thread(() -> {
            try {
                // Wait for simulation to start
                Thread.sleep(warmUpPeriod * 1000);
                
                // Schedule failures throughout simulation
                for (int i = 0; i < simulationTime / 10; i++) { // Schedule failures every ~10 time units
                    if (random.nextDouble() < faultToleranceProbability) {
                        // Select random node
                        int nodeId = random.nextInt(edgeNodes.size());
                        
                        // Random recovery time between 2-5 time units
                        long recoveryTime = (2 + random.nextInt(4)) * 1000;
                        
                        System.out.println("Scheduling failure for node " + nodeId + 
                                         " with recovery after " + recoveryTime + "ms");
                        
                        // Simulate failure
                        edgeController.simulateNodeFailure(nodeId, recoveryTime);
                    }
                    
                    // Wait before next potential failure
                    Thread.sleep(10 * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Run the simulation
     */
    public void runSimulation() {
        System.out.println("Starting Edge-Fog Computing Simulation...");
        System.out.println("Simulation time: " + simulationTime + " seconds");
        System.out.println("Number of IoT devices: " + iotDevices.size());
        System.out.println("Number of Edge nodes: " + edgeNodes.size());
        
        // Simulation time steps
        for (int time = 0; time < simulationTime; time++) {
            System.out.println("\nTime step: " + time);
            
            // Generate tasks from IoT devices
            generateTasks(time);
            
            // Process tasks on edge nodes and cloud
            processTasks(time);
            
            // Update device locations
            if (time % locationCheckInterval == 0) {
                updateDeviceLocations();
            }
            
            // Collect statistics (after warm-up period)
            if (time >= warmUpPeriod) {
                collectStatistics(time);
            }
        }
        
        // Print final statistics
        printFinalStatistics();
        
        // Save results to file if enabled
        if (fileLogEnabled) {
            saveResultsToFile();
        }
    }
    
    /**
     * Generate tasks from IoT devices
     * @param time Current simulation time
     */
    private void generateTasks(int time) {
        for (IoTDevice device : iotDevices) {
            // Skip devices with depleted batteries
            if (advancedFeaturesEnabled && device.getBatteryLevel() <= 0.05) {
                System.out.println("Device " + device.getDeviceId() + " has low battery, skipping task generation");
                continue;
            }
            
            // Simple task generation based on probability
            if (random.nextDouble() < 0.1) { // 10% chance to generate a task each time step
                Task task;
                
                if (advancedFeaturesEnabled && securityEnabled) {
                    // Generate task with security features
                    int appType = random.nextInt(numOfAppTypes);
                    
                    // Create security metadata
                    Map<String, Object> securityMetadata = new HashMap<>();
                    securityMetadata.put("securityLevel", securityLevel > 1 ? "high" : "standard");
                    securityMetadata.put("encryptionRequired", random.nextBoolean());
                    
                    // Generate secure task
                    security.SecurityManager securityManager = edgeController.getSecurityManager();
                    task = device.generateTask(appType, securityManager, securityMetadata);
                } else {
                    // Generate basic task
                    int appType = random.nextInt(numOfAppTypes);
                    task = device.generateTask(appType);
                }
                
                System.out.println("Device " + device.getDeviceId() + " generated task " + task.getTaskId());
                
                // Make offloading decision with advanced features
                String offloadingDecision;
                if (advancedFeaturesEnabled) {
                    offloadingDecision = edgeController.makeOffloadingDecision(task, device);
                } else {
                    offloadingDecision = edgeController.makeOffloadingDecision(task);
                }
                
                System.out.println("Offloading decision for task " + task.getTaskId() + ": " + offloadingDecision);
                
                // Process task based on decision with advanced features
                boolean success = true;
                if (advancedFeaturesEnabled) {
                    success = edgeController.processTask(task, device, offloadingDecision);
                    
                    // Update energy statistics
                    edgeController.updateEnergyStatistics(device);
                } else {
                    edgeController.processTask(task, offloadingDecision);
                }
                
                if (!success) {
                    System.out.println("Failed to process task " + task.getTaskId() + " due to resource constraints");
                }
            }
            
            // Update device energy consumption during idle time
            if (advancedFeaturesEnabled) {
                device.updateBatteryForIdleTime(1); // 1 second of idle time
            }
        }
    }
    
    /**
     * Process tasks on edge nodes and cloud
     * @param time Current simulation time
     */
    private void processTasks(int time) {
        // Process tasks on edge nodes
        for (EdgeNode edge : edgeNodes) {
            // Skip unhealthy nodes
            if (advancedFeaturesEnabled && !edge.isHealthy()) {
                continue;
            }
            
            Task processedTask = edge.processNextTask(time);
            if (processedTask != null) {
                System.out.println("Edge " + edge.getNodeId() + " processed task " + processedTask.getTaskId());
                
                // Handle analytics results if enabled
                if (advancedFeaturesEnabled && realTimeAnalyticsEnabled) {
                    Map<String, Object> analyticsResults = edge.getAnalyticsResults();
                    if (analyticsResults != null && !analyticsResults.isEmpty()) {
                        System.out.println("Analytics results from Edge " + edge.getNodeId() + ": " + 
                                         analyticsResults.get("summary"));
                    }
                }
            }
        }
        
        // Process tasks on cloud
        Task processedTask = cloud.processNextTask(time);
        if (processedTask != null) {
            System.out.println("Cloud processed task " + processedTask.getTaskId());
        }
        
        // Perform service discovery updates if enabled
        if (advancedFeaturesEnabled && time % serviceDiscoveryInterval == 0) {
            updateServiceDiscovery();
        }
    }
    
    /**
     * Update service discovery information across the system
     */
    private void updateServiceDiscovery() {
        // Find all available services
        List<ServiceRegistration> computationServices = edgeController.findServices("computation");
        List<ServiceRegistration> storageServices = edgeController.findServices("storage");
        
        System.out.println("Service Discovery Update:");
        System.out.println("- Computation services available: " + computationServices.size());
        System.out.println("- Storage services available: " + storageServices.size());
        
        // Check for service migrations if needed
        for (EdgeNode edge : edgeNodes) {
            if (edge.getCpuUtilization() > 80) { // High utilization
                System.out.println("Edge " + edge.getNodeId() + " is highly utilized, considering service migration");
            }
        }
    }
    
    /**
     * Update IoT device locations
     */
    private void updateDeviceLocations() {
        SimulationArea area = new SimulationArea(500, 500);
        for (IoTDevice device : iotDevices) {
            device.updateLocation(area);
        }
    }
    
    /**
     * Collect statistics at current time step
     * @param time Current simulation time
     */
    private void collectStatistics(int time) {
        Map<String, Object> stats = edgeController.getSystemStatistics();
        stats.put("timeStep", (double) time);
        
        // Collect additional statistics for advanced features
        if (advancedFeaturesEnabled) {
            // Battery statistics
            double avgBatteryLevel = 0.0;
            int lowBatteryDevices = 0;
            
            for (IoTDevice device : iotDevices) {
                avgBatteryLevel += device.getBatteryLevel();
                if (device.getBatteryLevel() < 0.2) {
                    lowBatteryDevices++;
                }
            }
            
            avgBatteryLevel /= iotDevices.size();
            stats.put("averageBatteryLevel", avgBatteryLevel);
            stats.put("lowBatteryDevices", lowBatteryDevices);
            
            // Protocol statistics
            Map<String, Integer> protocolUsage = new HashMap<>();
            for (IoTDevice device : iotDevices) {
                String protocolType = device.getWirelessProtocol().getType();
                protocolUsage.put(protocolType, protocolUsage.getOrDefault(protocolType, 0) + 1);
            }
            stats.put("protocolUsage", protocolUsage);
        }
        
        statisticsHistory.add(stats);
    }
    
    /**
     * Print final simulation statistics
     */
    private void printFinalStatistics() {
        if (statisticsHistory.isEmpty()) {
            System.out.println("No statistics collected.");
            return;
        }
        
        Map<String, Object> finalStats = statisticsHistory.get(statisticsHistory.size() - 1);
        
        System.out.println("\n===== Final Statistics =====");
        System.out.println("Average service time (Local Edge): " + finalStats.get("localEdgeServiceTime") + " ms");
        System.out.println("Average service time (Other Edge): " + finalStats.get("otherEdgeServiceTime") + " ms");
        System.out.println("Average service time (Cloud): " + finalStats.get("cloudServiceTime") + " ms");
        System.out.println("Average Edge Utilization: " + finalStats.get("averageEdgeUtilization") + "%");
        System.out.println("Cloud Utilization: " + finalStats.get("cloudUtilization") + "%");
        
        // Print advanced statistics if enabled
        if (advancedFeaturesEnabled) {
            System.out.println("\n===== Advanced Statistics =====");
            
            // Energy statistics
            System.out.println("\nEnergy Statistics:");
            System.out.println("Total Edge Energy Consumed: " + finalStats.get("totalEdgeEnergyConsumed") + " units");
            System.out.println("Total System Energy Consumed: " + finalStats.get("totalSystemEnergyConsumed") + " units");
            System.out.println("Average Battery Level: " + finalStats.get("averageBatteryLevel") + "%");
            System.out.println("Low Battery Devices: " + finalStats.get("lowBatteryDevices"));
            
            // Security statistics
            System.out.println("\nSecurity Statistics:");
            System.out.println("Security Incidents Detected: " + finalStats.get("securityIncidentsDetected"));
            
            // Service management statistics
            System.out.println("\nService Management Statistics:");
            System.out.println("Successful Migrations: " + finalStats.get("successfulMigrations"));
            System.out.println("Failed Migrations: " + finalStats.get("failedMigrations"));
            System.out.println("Recovered Tasks: " + finalStats.get("recoveredTasks"));
            System.out.println("Registered Services: " + finalStats.get("registeredServices"));
            
            // Protocol usage statistics
            System.out.println("\nWireless Protocol Usage:");
            Map<String, Integer> protocolUsage = (Map<String, Integer>) finalStats.get("protocolUsage");
            if (protocolUsage != null) {
                for (Map.Entry<String, Integer> entry : protocolUsage.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + " devices");
                }
            }
        }
    }
    
    /**
     * Save simulation results to CSV file
     */
    private void saveResultsToFile() {
        try {
            // Create basic results file
            FileWriter basicWriter = new FileWriter("results/simulation_results.csv");
            
            // Write header for basic metrics
            basicWriter.append("TimeStep,LocalEdgeServiceTime,OtherEdgeServiceTime,CloudServiceTime,AvgEdgeUtilization,CloudUtilization\n");
            
            // Write data for basic metrics
            for (Map<String, Object> stats : statisticsHistory) {
                basicWriter.append(String.valueOf(stats.get("timeStep")));
                basicWriter.append(",");
                basicWriter.append(String.valueOf(stats.get("localEdgeServiceTime")));
                basicWriter.append(",");
                basicWriter.append(String.valueOf(stats.get("otherEdgeServiceTime")));
                basicWriter.append(",");
                basicWriter.append(String.valueOf(stats.get("cloudServiceTime")));
                basicWriter.append(",");
                basicWriter.append(String.valueOf(stats.get("averageEdgeUtilization")));
                basicWriter.append(",");
                basicWriter.append(String.valueOf(stats.get("cloudUtilization")));
                basicWriter.append("\n");
            }
            
            basicWriter.flush();
            basicWriter.close();
            System.out.println("Basic results saved to results/simulation_results.csv");
            
            // Create advanced results file if enabled
            if (advancedFeaturesEnabled) {
                FileWriter advancedWriter = new FileWriter("results/advanced_results.csv");
                
                // Write header for advanced metrics
                advancedWriter.append("TimeStep,TotalEdgeEnergy,TotalSystemEnergy,AvgBatteryLevel,LowBatteryDevices," +
                                     "SecurityIncidents,SuccessfulMigrations,FailedMigrations,RecoveredTasks\n");
                
                // Write data for advanced metrics
                for (Map<String, Object> stats : statisticsHistory) {
                    advancedWriter.append(String.valueOf(stats.get("timeStep")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("totalEdgeEnergyConsumed")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("totalSystemEnergyConsumed")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("averageBatteryLevel")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("lowBatteryDevices")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("securityIncidentsDetected")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("successfulMigrations")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("failedMigrations")));
                    advancedWriter.append(",");
                    advancedWriter.append(String.valueOf(stats.get("recoveredTasks")));
                    advancedWriter.append("\n");
                }
                
                advancedWriter.flush();
                advancedWriter.close();
                System.out.println("Advanced results saved to results/advanced_results.csv");
                
                // Create protocol usage statistics file
                FileWriter protocolWriter = new FileWriter("results/protocol_usage.csv");
                protocolWriter.append("TimeStep,WiFi,LoRaWAN,NB-IoT,5G\n");
                
                for (Map<String, Object> stats : statisticsHistory) {
                    protocolWriter.append(String.valueOf(stats.get("timeStep")));
                    
                    Map<String, Integer> protocolUsage = (Map<String, Integer>) stats.get("protocolUsage");
                    if (protocolUsage != null) {
                        protocolWriter.append(",");
                        protocolWriter.append(String.valueOf(protocolUsage.getOrDefault("WiFi", 0)));
                        protocolWriter.append(",");
                        protocolWriter.append(String.valueOf(protocolUsage.getOrDefault("LoRaWAN", 0)));
                        protocolWriter.append(",");
                        protocolWriter.append(String.valueOf(protocolUsage.getOrDefault("NB-IoT", 0)));
                        protocolWriter.append(",");
                        protocolWriter.append(String.valueOf(protocolUsage.getOrDefault("5G", 0)));
                    } else {
                        protocolWriter.append(",0,0,0,0");
                    }
                    
                    protocolWriter.append("\n");
                }
                
                protocolWriter.flush();
                protocolWriter.close();
                System.out.println("Protocol usage statistics saved to results/protocol_usage.csv");
            }
            
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run the simulation
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        String configFile = "simulation/default_config.properties";
        if (args.length > 0) {
            configFile = args[0];
        }
        
        EdgeFogSimulation simulation = new EdgeFogSimulation(configFile);
        simulation.initialize();
        simulation.runSimulation();
    }
}


