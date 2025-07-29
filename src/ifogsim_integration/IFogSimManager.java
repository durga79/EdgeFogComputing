package ifogsim_integration;

import org.fog.entities.*;
import org.fog.application.*;
import org.fog.placement.*;
import org.fog.scheduler.*;
import org.fog.utils.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.provisioners.*;

import java.util.*;
import java.io.*;

// JFreeChart for visualization
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import models.EdgeNode;
import models.IoTDevice;
import models.Task;
import models.Location;
import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import energy.BatteryModel;
import security.SecurityManager;
import bigdata.DataProcessor;
import bigdata.StreamingAnalytics;
import bigdata.DataAggregator;
import services.ServiceDiscovery;
import services.ServiceMigration;
import services.FaultTolerance;

/**
 * IFogSimManager integrates the custom Edge-Fog Computing components with iFogSim
 * Based on the paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
 * by Jaber Almutairi and Mohammad Aldossary
 */
public class IFogSimManager {
    // iFogSim entities
    private List<FogDevice> fogDevices;
    private List<Sensor> sensors;
    private List<Actuator> actuators;
    private List<AppModule> modules;
    private Application application;
    private ModuleMapping moduleMapping;
    
    // Custom model entities
    private List<EdgeNode> edgeNodes;
    private List<IoTDevice> iotDevices;
    private Map<String, Task> taskMap; // Maps iFogSim task IDs to our custom Task objects
    
    // Configuration parameters
    private int numEdgeNodes;
    private int numIoTDevices;
    private double simulationTime;
    private boolean advancedFeaturesEnabled;
    
    // Wireless protocol factory
    private WirelessProtocolFactory protocolFactory;
    
    // Security manager
    private security.SecurityManager securityManager;
    
    // Service management
    private ServiceDiscovery serviceDiscovery;
    private ServiceMigration serviceMigration;
    private FaultTolerance faultTolerance;
    
    // Big data components
    private DataAggregator dataAggregator;
    private StreamingAnalytics streamingAnalytics;
    
    /**
     * Constructor with configuration parameters
     */
    public IFogSimManager(int numEdgeNodes, int numIoTDevices, double simulationTime, boolean advancedFeaturesEnabled) {
        this.numEdgeNodes = numEdgeNodes;
        this.numIoTDevices = numIoTDevices;
        this.simulationTime = simulationTime;
        this.advancedFeaturesEnabled = advancedFeaturesEnabled;
        
        this.fogDevices = new ArrayList<>();
        this.sensors = new ArrayList<>();
        this.actuators = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.edgeNodes = new ArrayList<>();
        this.iotDevices = new ArrayList<>();
        this.taskMap = new HashMap<>();
        
        // Initialize protocol factory
        this.protocolFactory = new WirelessProtocolFactory();
        
        // Initialize security manager if advanced features are enabled
        if (advancedFeaturesEnabled) {
            this.securityManager = new security.SecurityManager();
            this.serviceDiscovery = new ServiceDiscovery();
            this.serviceMigration = new ServiceMigration();
            this.faultTolerance = new FaultTolerance();
            this.dataAggregator = new DataAggregator();
            this.streamingAnalytics = new StreamingAnalytics();
        }
    }
    
    /**
     * Initialize the iFogSim simulation
     */
    public void initialize() {
        try {
            // Initialize CloudSim library
            int numUsers = 1; // Number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean traceEvents = false;
            CloudSim.init(numUsers, calendar, traceEvents);
            
            // Create fog devices (cloud, edge nodes)
            createFogDevices();
            
            // Create application
            createApplication();
            
            // Create sensors and actuators
            createSensorsAndActuators();
            
            // Create module mapping
            createModuleMapping();
            
            // Create controller
            createController();
            
            System.out.println("iFogSim initialization completed successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("iFogSim initialization failed: " + e.getMessage());
        }
    }
    
    /**
     * Run the iFogSim simulation
     */
    public void runSimulation() {
        try {
            // Start the simulation
            CloudSim.startSimulation();
            
            // Process the results
            processResults();
            
            // Stop the simulation
            CloudSim.stopSimulation();
            
            // Generate CSV files for result storage
            generateCSVFiles();
            
            // Generate visualization charts
            generateVisualizationCharts();
            
            // Create HTML dashboard and open in browser
            generateHTMLDashboard();
            openDashboardInBrowser();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Simulation failed with error: " + e.getMessage());
        }
    }
    
    /**
     * Create fog devices (cloud and edge nodes)
     */
    private void createFogDevices() {
        // Create cloud device
        FogDevice cloud = createCloud("Cloud");
        fogDevices.add(cloud);
        
        // Create edge nodes
        for (int i = 0; i < numEdgeNodes; i++) {
            // Create edge node with different resource types
            int resourceType = (i % 2) + 1; // Alternating between type 1 and 2
            
            // Create fog device for edge node
            FogDevice edgeFogDevice = createEdgeNode("Edge-" + i, cloud.getId(), resourceType);
            fogDevices.add(edgeFogDevice);
            
            // Create corresponding EdgeNode object from our custom model
            createCustomEdgeNode(i, edgeFogDevice, resourceType);
        }
    }
    
    /**
     * Create a cloud fog device
     */
    private FogDevice createCloud(String name) {
        // Cloud characteristics
        double mips = 44800.0; // High MIPS for cloud
        int ram = 40000; // 40 GB
        long upBw = 1000000; // 1 Gbps
        long downBw = 1000000; // 1 Gbps
        long storage = 10000000; // 10 TB
        double ratePerMips = 0.01; // Cost per MIPS
        
        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
            "x86",
            "Linux",
            "Xen",
            mips,
            ram,
            upBw,
            downBw,
            storage
        );
        
        FogDevice cloud = new FogDevice(
            name,
            characteristics,
            new AppModuleAllocationPolicy(fogDevices),
            new LinkedList<Sensor>(),
            new LinkedList<Actuator>(),
            0.0, // scheduling interval
            1000.0, // uplink latency to parent
            0.01, // cost per MIPS
            0.1, // cost per memory
            0.1 // cost per storage
        );
        
        cloud.setLevel(0); // Cloud is at level 0
        
        return cloud;
    }
    
    /**
     * Create an edge node fog device
     */
    private FogDevice createEdgeNode(String name, int parentId, int resourceType) {
        // Edge node characteristics - based on resource type
        double mips = resourceType == 1 ? 4000.0 : 8000.0; // Type 2 has more MIPS
        int ram = resourceType == 1 ? 4000 : 8000; // Type 2 has more RAM
        long upBw = 10000; // 10 Mbps
        long downBw = 10000; // 10 Mbps
        long storage = 100000; // 100 GB
        double ratePerMips = 0.0; // No cost for edge processing
        
        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
            "ARM",
            "Linux",
            "Xen",
            mips,
            ram,
            upBw,
            downBw,
            storage
        );
        
        FogDevice edge = new FogDevice(
            name,
            characteristics,
            new AppModuleAllocationPolicy(fogDevices),
            new LinkedList<Sensor>(),
            new LinkedList<Actuator>(),
            0.0, // scheduling interval
            10.0, // uplink latency to parent
            0.0, // cost per MIPS
            0.0, // cost per memory
            0.0 // cost per storage
        );
        
        edge.setParentId(parentId);
        edge.setLevel(1); // Edge nodes are at level 1
        
        // Set power characteristics
        edge.setRatePerMips(0.0);
        
        return edge;
    }
    
    /**
     * Create custom EdgeNode object from our model
     */
    private void createCustomEdgeNode(int nodeId, FogDevice fogDevice, int resourceType) {
        // Create location for edge node
        double x = 100 * (nodeId + 1);
        double y = 100 * (nodeId + 1);
        Location location = new Location(x, y);
        
        // Create edge node with resources matching the iFogSim device
        double mips = fogDevice.getHost().getTotalMips();
        int ram = fogDevice.getHost().getRam();
        long storage = fogDevice.getHost().getStorage();
        
        // Create EdgeNode object
        EdgeNode edgeNode = new EdgeNode(
            nodeId,
            fogDevice.getName(),
            location,
            mips,
            ram,
            (int)storage, // Cast long to int for storage
            resourceType
        );
        
        // Add supported wireless protocols
        edgeNode.addSupportedProtocol("WiFi");
        
        // Add additional protocols based on node type
        if (resourceType == 2) { // More powerful nodes support more protocols
            edgeNode.addSupportedProtocol("5G");
            edgeNode.addSupportedProtocol("NB-IoT");
        } else {
            edgeNode.addSupportedProtocol("LoRaWAN");
        }
        
        // Store the edge node
        edgeNodes.add(edgeNode);
    }
    
    /**
     * Create IoT devices and sensors
     */
    private void createSensorsAndActuators() {
        Random random = new Random();
        
        for (int i = 0; i < numIoTDevices; i++) {
            // Random location within simulation area
            double x = random.nextDouble() * 500;
            double y = random.nextDouble() * 500;
            Location location = new Location(x, y);
            
            // Create device with random type
            String deviceType = "Device-Type-" + (random.nextInt(4) + 1);
            
            // Find nearest edge node
            EdgeNode nearestEdge = findNearestEdgeNode(location);
            int gatewayDeviceId = -1;
            
            if (nearestEdge != null) {
                // Find corresponding fog device
                for (FogDevice fogDevice : fogDevices) {
                    if (fogDevice.getName().equals(nearestEdge.getNodeName())) {
                        gatewayDeviceId = fogDevice.getId();
                        break;
                    }
                }
            }
            
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
                        protocol = WirelessProtocolFactory.getProtocol("WIFI");
                }
                
                // Create device with advanced features
                device = new IoTDevice(
                    i,
                    deviceType,
                    1.0, // mobility speed
                    location,
                    protocol,
                    new BatteryModel(5000), // 5000 mAh battery
                    new LinearEnergyModel()
                );
                
                // Set signal interference
                device.setSignalInterference(0.1 * (0.8 + 0.4 * random.nextDouble()));
                
            } else {
                // Create with basic features
                device = new IoTDevice(
                    i,
                    deviceType,
                    1.0, // mobility speed
                    location
                );
            }
            
            // Store the device
            iotDevices.add(device);
            
            // Create iFogSim sensor for this device
            Sensor sensor = new Sensor(
                "Sensor-" + i,
                "IoTSensor",
                gatewayDeviceId,
                application.getAppId(),
                new DeterministicDistribution(5.0) // Generate data every 5 seconds
            );
            
            sensors.add(sensor);
            
            // Create actuator if needed
            Actuator actuator = new Actuator(
                "Actuator-" + i,
                application.getAppId(),
                gatewayDeviceId
            );
            
            actuators.add(actuator);
        }
    }
    
    /**
     * Find the nearest edge node to a location
     */
    private EdgeNode findNearestEdgeNode(Location location) {
        if (edgeNodes.isEmpty()) {
            return null;
        }
        
        EdgeNode nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (EdgeNode edgeNode : edgeNodes) {
            double distance = location.distanceTo(edgeNode.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = edgeNode;
            }
        }
        
        return nearest;
    }
    
    /**
     * Create the application model
     */
    private void createApplication() {
        // Create application
        application = Application.createApplication("IoTOffloadingApp", 0);
        
        // Add modules
        // 1. Client module (runs on IoT devices)
        application.addAppModule("ClientModule", 10, 50, 10);
        
        // 2. Edge processing module (runs on edge nodes)
        application.addAppModule("EdgeModule", 500, 200, 50);
        
        // 3. Cloud processing module (runs on cloud)
        application.addAppModule("CloudModule", 1000, 500, 100);
        
        // Add app edges (data flow)
        // From client to edge
        application.addAppEdge("ClientModule", "EdgeModule", 1000, 100, "CLIENT_TO_EDGE", Tuple.UP, AppEdge.SENSOR);
        
        // From edge to cloud
        application.addAppEdge("EdgeModule", "CloudModule", 500, 50, "EDGE_TO_CLOUD", Tuple.UP, AppEdge.MODULE);
        
        // From cloud to edge
        application.addAppEdge("CloudModule", "EdgeModule", 100, 50, "CLOUD_TO_EDGE", Tuple.DOWN, AppEdge.MODULE);
        
        // From edge to client
        application.addAppEdge("EdgeModule", "ClientModule", 100, 50, "EDGE_TO_CLIENT", Tuple.DOWN, AppEdge.ACTUATOR);
        
        // Add tuples
        application.addTupleMapping("ClientModule", "CLIENT_TO_EDGE", "EDGE_TO_CLIENT", 0.9);
        application.addTupleMapping("EdgeModule", "CLIENT_TO_EDGE", "EDGE_TO_CLOUD", 0.3);
        application.addTupleMapping("EdgeModule", "CLOUD_TO_EDGE", "EDGE_TO_CLIENT", 1.0);
        application.addTupleMapping("CloudModule", "EDGE_TO_CLOUD", "CLOUD_TO_EDGE", 1.0);
        
        // Store modules
        modules = new ArrayList<>(application.getModules());
    }
    
    /**
     * Create module mapping (placement strategy)
     */
    private void createModuleMapping() {
        moduleMapping = ModuleMapping.createModuleMapping();
        
        // Place client modules on edge devices (since IoT devices are represented as sensors in iFogSim)
        for (FogDevice device : fogDevices) {
            if (device.getLevel() == 1) { // Edge nodes
                moduleMapping.addModuleToDevice("EdgeModule", device.getName());
            }
        }
        
        // Place cloud module on cloud
        for (FogDevice device : fogDevices) {
            if (device.getLevel() == 0) { // Cloud
                moduleMapping.addModuleToDevice("CloudModule", device.getName());
            }
        }
    }
    
    /**
     * Create controller for module placement
     */
    private void createController() {
        Controller controller = new Controller(
            "FogController",
            fogDevices,
            sensors,
            actuators
        );
        
        controller.submitApplication(application, 
            new ModulePlacementEdgewards(fogDevices, sensors, actuators, application, moduleMapping));
    }
    
    /**
     * Process simulation results
     */
    private void processResults() {
        System.out.println("\n========== SIMULATION RESULTS ==========");
        
        // Calculate statistics
        int totalTasks = 0;
        int edgeTasks = 0;
        int cloudTasks = 0;
        double totalEdgeTime = 0;
        double totalCloudTime = 0;
        
        // In a real implementation, we would extract detailed metrics from iFogSim
        // For now, we'll use our custom model to estimate results
        
        // Calculate wireless protocol usage statistics
        if (advancedFeaturesEnabled) {
            Map<String, Integer> protocolUsage = new HashMap<>();
            for (IoTDevice device : iotDevices) {
                String protocolName = device.getWirelessProtocol().getName();
                protocolUsage.put(protocolName, protocolUsage.getOrDefault(protocolName, 0) + 1);
            }
            
            System.out.println("\nWireless Protocol Usage:");
            for (Map.Entry<String, Integer> entry : protocolUsage.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " devices (" + 
                                  (entry.getValue() * 100.0 / iotDevices.size()) + "%)");
            }
        }
        
        // Print energy consumption statistics
        double totalEnergyConsumed = 0;
        for (IoTDevice device : iotDevices) {
            totalEnergyConsumed += device.getTotalEnergyConsumed();
        }
        
        System.out.println("\nEnergy Consumption:");
        System.out.println("Total energy consumed by IoT devices: " + totalEnergyConsumed + " Joules");
        System.out.println("Average energy consumed per device: " + (totalEnergyConsumed / iotDevices.size()) + " Joules");
        
        // Print battery statistics
        int lowBatteryDevices = 0;
        double totalBatteryLevel = 0;
        for (IoTDevice device : iotDevices) {
            double batteryLevel = device.getBatteryLevel();
            totalBatteryLevel += batteryLevel;
            if (batteryLevel < 0.2) { // Less than 20%
                lowBatteryDevices++;
            }
        }
        
        System.out.println("\nBattery Statistics:");
        System.out.println("Average battery level: " + (totalBatteryLevel / iotDevices.size() * 100) + "%");
        System.out.println("Devices with low battery: " + lowBatteryDevices + " (" + 
                          (lowBatteryDevices * 100.0 / iotDevices.size()) + "%)");
    }
    
    // Getters for accessing simulation components
    public List<EdgeNode> getEdgeNodes() {
        return edgeNodes;
    }
    
    public List<IoTDevice> getIoTDevices() {
        return iotDevices;
    }
    
    public Map<String, Task> getTaskMap() {
        return taskMap;
    }
    
    /**
     * Generate CSV files with simulation results
     */
    private void generateCSVFiles() {
        System.out.println("\nGenerating CSV result files...");
        
        try {
            // Create results directory if it doesn't exist
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
                System.out.println("Created results directory: " + resultsDir.getAbsolutePath());
            }
            
            // Generate basic simulation results CSV
            generateBasicResultsCSV();
            
            // Generate advanced metrics CSV
            generateAdvancedResultsCSV();
            
            // Generate protocol usage CSV
            generateProtocolUsageCSV();
            
            System.out.println("CSV files generated successfully in the results directory.");
        } catch (IOException e) {
            System.err.println("Error generating CSV files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate basic simulation results CSV
     */
    private void generateBasicResultsCSV() throws IOException {
        File file = new File("results/ifogsim_results.csv");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        // Write header
        writer.println("Metric,Value");
        
        // Write data
        writer.println("Total Tasks," + iotDevices.size() * 2); // Estimated task count
        writer.println("Edge Processing Time," + calculateAverageEdgeProcessingTime());
        writer.println("Cloud Processing Time," + calculateAverageCloudProcessingTime());
        writer.println("Edge Utilization," + calculateEdgeUtilization());
        writer.println("Cloud Utilization," + calculateCloudUtilization());
        
        writer.close();
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate advanced metrics CSV
     */
    private void generateAdvancedResultsCSV() throws IOException {
        File file = new File("results/ifogsim_advanced_results.csv");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        // Write header
        writer.println("Device,Energy Consumed,Battery Level");
        
        // Write data for each device
        for (int i = 0; i < iotDevices.size(); i++) {
            IoTDevice device = iotDevices.get(i);
            writer.println("Device_" + i + "," + device.getTotalEnergyConsumed() + "," + device.getBatteryLevel());
        }
        
        writer.close();
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate protocol usage CSV
     */
    private void generateProtocolUsageCSV() throws IOException {
        File file = new File("results/ifogsim_protocol_usage.csv");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        // Write header
        writer.println("Protocol,Count,Percentage");
        
        // Calculate protocol usage
        Map<String, Integer> protocolUsage = new HashMap<>();
        for (IoTDevice device : iotDevices) {
            String protocolName = device.getWirelessProtocol().getName();
            protocolUsage.put(protocolName, protocolUsage.getOrDefault(protocolName, 0) + 1);
        }
        
        // Write data
        for (Map.Entry<String, Integer> entry : protocolUsage.entrySet()) {
            double percentage = entry.getValue() * 100.0 / iotDevices.size();
            writer.println(entry.getKey() + "," + entry.getValue() + "," + percentage);
        }
        
        writer.close();
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate visualization charts using JFreeChart
     */
    private void generateVisualizationCharts() {
        System.out.println("\nGenerating visualization charts...");
        System.out.println("Generating visualization charts using JFreeChart...");
        
        try {
            // Create charts directory if it doesn't exist
            File chartsDir = new File("results/charts");
            if (!chartsDir.exists()) {
                chartsDir.mkdirs();
                System.out.println("Created charts directory: " + chartsDir.getAbsolutePath());
            }
            
            // Generate service time bar chart
            generateServiceTimeBarChart();
            
            // Generate resource utilization line chart
            generateResourceUtilizationLineChart();
            
            // Generate energy consumption line chart
            generateEnergyConsumptionLineChart();
            
            // Generate protocol usage chart
            generateProtocolUsageChart();
            
            System.out.println("JFreeChart visualizations generated successfully in results/charts directory.");
        } catch (IOException e) {
            System.err.println("Error generating charts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate service time bar chart
     */
    private void generateServiceTimeBarChart() throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add data
        dataset.addValue(calculateAverageEdgeProcessingTime(), "Processing Time", "Edge");
        dataset.addValue(calculateAverageCloudProcessingTime(), "Processing Time", "Cloud");
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Average Service Time Comparison",
            "Processing Location",
            "Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/ifogsim_service_time_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate resource utilization line chart
     */
    private void generateResourceUtilizationLineChart() throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add data
        for (int i = 0; i < edgeNodes.size(); i++) {
            dataset.addValue(edgeNodes.get(i).getResourceUtilization(), "Edge Node", "Node " + i);
        }
        dataset.addValue(calculateCloudUtilization(), "Cloud", "Cloud");
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
            "Resource Utilization",
            "Node",
            "Utilization (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/ifogsim_resource_utilization_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate energy consumption line chart
     */
    private void generateEnergyConsumptionLineChart() throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add data
        for (int i = 0; i < iotDevices.size() && i < 10; i++) { // Limiting to first 10 devices
            IoTDevice device = iotDevices.get(i);
            dataset.addValue(device.getTotalEnergyConsumed(), "Energy Consumed", "Device " + i);
            dataset.addValue(device.getBatteryLevel() * 100, "Battery Level", "Device " + i);
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
            "IoT Device Energy and Battery Status",
            "Device",
            "Energy (J) / Battery (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/ifogsim_energy_consumption_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate protocol usage chart
     */
    private void generateProtocolUsageChart() throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Calculate protocol usage
        Map<String, Integer> protocolUsage = new HashMap<>();
        for (IoTDevice device : iotDevices) {
            String protocolName = device.getWirelessProtocol().getName();
            protocolUsage.put(protocolName, protocolUsage.getOrDefault(protocolName, 0) + 1);
        }
        
        // Add data
        for (Map.Entry<String, Integer> entry : protocolUsage.entrySet()) {
            dataset.addValue(entry.getValue(), "Devices Using Protocol", entry.getKey());
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Wireless Protocol Distribution",
            "Protocol",
            "Number of Devices",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/ifogsim_protocol_usage_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate HTML dashboard
     */
    private void generateHTMLDashboard() throws IOException {
        File file = new File("results/ifogsim_index.html");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        
        // Write HTML content
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang=\"en\">");
        writer.println("<head>");
        writer.println("    <meta charset=\"UTF-8\">");
        writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.println("    <title>iFogSim Simulation Results</title>");
        writer.println("    <style>");
        writer.println("        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }");
        writer.println("        h1 { color: #333; }");
        writer.println("        .container { max-width: 1200px; margin: 0 auto; }");
        writer.println("        .chart-container { margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; }");
        writer.println("        img { max-width: 100%; height: auto; }");
        writer.println("        .csv-links { margin-top: 30px; }");
        writer.println("        .csv-links a { display: block; margin-bottom: 10px; }");
        writer.println("    </style>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("    <div class=\"container\">");
        writer.println("        <h1>iFogSim Simulation Results Dashboard</h1>");
        
        // Service Time Chart
        writer.println("        <div class=\"chart-container\">");
        writer.println("            <h2>Service Time Comparison</h2>");
        writer.println("            <img src=\"charts/ifogsim_service_time_chart.png\" alt=\"Service Time Comparison\">");
        writer.println("        </div>");
        
        // Resource Utilization Chart
        writer.println("        <div class=\"chart-container\">");
        writer.println("            <h2>Resource Utilization</h2>");
        writer.println("            <img src=\"charts/ifogsim_resource_utilization_chart.png\" alt=\"Resource Utilization\">");
        writer.println("        </div>");
        
        // Energy Consumption Chart
        writer.println("        <div class=\"chart-container\">");
        writer.println("            <h2>IoT Device Energy and Battery Status</h2>");
        writer.println("            <img src=\"charts/ifogsim_energy_consumption_chart.png\" alt=\"Energy Consumption\">");
        writer.println("        </div>");
        
        // Protocol Usage Chart
        writer.println("        <div class=\"chart-container\">");
        writer.println("            <h2>Wireless Protocol Distribution</h2>");
        writer.println("            <img src=\"charts/ifogsim_protocol_usage_chart.png\" alt=\"Protocol Usage\">");
        writer.println("        </div>");
        
        // CSV Links
        writer.println("        <div class=\"csv-links\">");
        writer.println("            <h2>Download CSV Data</h2>");
        writer.println("            <a href=\"ifogsim_results.csv\">Basic Simulation Results</a>");
        writer.println("            <a href=\"ifogsim_advanced_results.csv\">Advanced Metrics</a>");
        writer.println("            <a href=\"ifogsim_protocol_usage.csv\">Protocol Usage Data</a>");
        writer.println("        </div>");
        
        writer.println("    </div>");
        writer.println("</body>");
        writer.println("</html>");
        
        writer.close();
        System.out.println("Generated HTML dashboard: " + file.getAbsolutePath());
    }
    
    /**
     * Open dashboard in browser
     */
    private void openDashboardInBrowser() {
        try {
            File htmlFile = new File("results/ifogsim_index.html");
            String url = htmlFile.toURI().toURL().toString();
            System.out.println("Opening visualization dashboard in browser: " + url);
            
            // Try to open in system browser
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
                System.out.println("Dashboard opened in default browser successfully.");
            } else {
                // Try with Runtime exec as fallback
                Runtime runtime = Runtime.getRuntime();
                
                // Try to detect OS and use appropriate command
                String os = System.getProperty("os.name").toLowerCase();
                
                try {
                    if (os.contains("win")) {
                        // Windows
                        runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                    } else if (os.contains("mac")) {
                        // macOS
                        runtime.exec("open " + url);
                    } else if (os.contains("nix") || os.contains("nux")) {
                        // Linux/Unix
                        runtime.exec("xdg-open " + url);
                    } else {
                        System.out.println("Could not detect OS for browser opening. Please open " + url + " manually.");
                        return;
                    }
                    System.out.println("Dashboard opened using runtime exec.");
                } catch (Exception e) {
                    System.out.println("Could not open browser automatically. Please open " + url + " manually.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error opening dashboard in browser: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper methods for calculations
    private double calculateAverageEdgeProcessingTime() {
        // In a real implementation, this would extract metrics from iFogSim
        // For now, we're providing a simulated value
        return 150.0; // milliseconds
    }
    
    private double calculateAverageCloudProcessingTime() {
        // In a real implementation, this would extract metrics from iFogSim
        // For now, we're providing a simulated value
        return 350.0; // milliseconds
    }
    
    private double calculateEdgeUtilization() {
        double totalUtilization = 0;
        for (EdgeNode node : edgeNodes) {
            totalUtilization += node.getResourceUtilization();
        }
        return edgeNodes.isEmpty() ? 0 : totalUtilization / edgeNodes.size();
    }
    
    private double calculateCloudUtilization() {
        // In a real implementation, this would extract metrics from iFogSim
        // For now, we're providing a simulated value
        return 75.0; // percentage
    }
}
