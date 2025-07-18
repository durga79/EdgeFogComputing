package simulation;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    
    // Simulation components
    private List<EdgeNode> edgeNodes;
    private Cloud cloud;
    private List<IoTDevice> iotDevices;
    private EdgeController edgeController;
    private Random random;
    
    // Results collection
    private List<Map<String, Double>> statisticsHistory;
    
    public EdgeFogSimulation(String configFile) {
        loadConfiguration(configFile);
        random = new Random();
        statisticsHistory = new ArrayList<>();
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
            
            EdgeNode edgeNode = new EdgeNode(
                i,
                "Edge-" + i,
                location,
                mips,
                ram,
                edgeVmStorage,
                resourceType
            );
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
            
            IoTDevice device = new IoTDevice(
                i,
                deviceType,
                speedOfMobileDevice,
                location
            );
            
            // Register device with edge controller
            edgeController.registerDevice(device);
            
            iotDevices.add(device);
        }
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
            // Simple task generation based on probability
            if (random.nextDouble() < 0.1) { // 10% chance to generate a task each time step
                Task task = device.generateTask();
                System.out.println("Device " + device.getDeviceId() + " generated task " + task.getTaskId());
                
                // Make offloading decision
                String offloadingDecision = edgeController.makeOffloadingDecision(task);
                System.out.println("Offloading decision for task " + task.getTaskId() + ": " + offloadingDecision);
                
                // Process task based on decision
                edgeController.processTask(task, offloadingDecision);
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
            Task processedTask = edge.processNextTask(time);
            if (processedTask != null) {
                System.out.println("Edge " + edge.getNodeId() + " processed task " + processedTask.getTaskId());
            }
        }
        
        // Process tasks on cloud
        Task processedTask = cloud.processNextTask(time);
        if (processedTask != null) {
            System.out.println("Cloud processed task " + processedTask.getTaskId());
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
        Map<String, Double> stats = edgeController.getSystemStatistics();
        stats.put("timeStep", (double) time);
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
        
        Map<String, Double> finalStats = statisticsHistory.get(statisticsHistory.size() - 1);
        
        System.out.println("\n===== Final Statistics =====");
        System.out.println("Average service time (Local Edge): " + finalStats.get("localEdgeServiceTime") + " ms");
        System.out.println("Average service time (Other Edge): " + finalStats.get("otherEdgeServiceTime") + " ms");
        System.out.println("Average service time (Cloud): " + finalStats.get("cloudServiceTime") + " ms");
        System.out.println("Average Edge Utilization: " + finalStats.get("averageEdgeUtilization") + "%");
        System.out.println("Cloud Utilization: " + finalStats.get("cloudUtilization") + "%");
    }
    
    /**
     * Save simulation results to CSV file
     */
    private void saveResultsToFile() {
        try {
            FileWriter writer = new FileWriter("results/simulation_results.csv");
            
            // Write header
            writer.append("TimeStep,LocalEdgeServiceTime,OtherEdgeServiceTime,CloudServiceTime,AvgEdgeUtilization,CloudUtilization\n");
            
            // Write data
            for (Map<String, Double> stats : statisticsHistory) {
                writer.append(String.valueOf(stats.get("timeStep")));
                writer.append(",");
                writer.append(String.valueOf(stats.get("localEdgeServiceTime")));
                writer.append(",");
                writer.append(String.valueOf(stats.get("otherEdgeServiceTime")));
                writer.append(",");
                writer.append(String.valueOf(stats.get("cloudServiceTime")));
                writer.append(",");
                writer.append(String.valueOf(stats.get("averageEdgeUtilization")));
                writer.append(",");
                writer.append(String.valueOf(stats.get("cloudUtilization")));
                writer.append("\n");
            }
            
            writer.flush();
            writer.close();
            System.out.println("Results saved to results/simulation_results.csv");
            
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


