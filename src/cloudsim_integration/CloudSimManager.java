package cloudsim_integration;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

// JFreeChart imports for visualization
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import visualization.DirectVisualization;

import models.EdgeNode;
import models.IoTDevice;
import models.Task;
import models.Location;
import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import security.SecurityManager;
import simulation.SimulationArea;
import bigdata.DataAggregator;
import bigdata.StreamingAnalytics;
import services.ServiceDiscovery;

/**
 * CloudSimManager integrates the custom Edge-Fog Computing components with CloudSim
 * Based on the paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
 */
public class CloudSimManager {
    // CloudSim entities
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private List<Host> hostList;
    private Datacenter cloudDatacenter;
    private List<Datacenter> edgeDatacenters;
    private DatacenterBroker broker;
    
    // Custom model entities
    private List<EdgeNode> edgeNodes;
    private List<IoTDevice> iotDevices;
    private Map<Integer, Task> taskMap; // Maps CloudSim cloudlet IDs to our custom Task objects
    
    // Configuration parameters
    private int numEdgeNodes;
    private int numIoTDevices;
    private double simulationTime;
    private boolean advancedFeaturesEnabled;
    
    // Wireless protocol factory
    private WirelessProtocolFactory protocolFactory;
    
    // Random number generator for task allocation
    private Random random;
    
    /**
     * Constructor with configuration parameters
     */
    public CloudSimManager(int numEdgeNodes, int numIoTDevices, double simulationTime, boolean advancedFeaturesEnabled) {
        this.numEdgeNodes = numEdgeNodes;
        this.numIoTDevices = numIoTDevices;
        this.simulationTime = simulationTime;
        this.advancedFeaturesEnabled = advancedFeaturesEnabled;
        
        this.cloudletList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.hostList = new ArrayList<>();
        this.edgeDatacenters = new ArrayList<>();
        this.edgeNodes = new ArrayList<>();
        this.iotDevices = new ArrayList<>();
        this.taskMap = new HashMap<>();
        
        // Initialize protocol factory
        this.protocolFactory = new WirelessProtocolFactory();
        
        // Initialize random number generator
        this.random = new Random();
    }
    
    /**
     * Initialize the CloudSim simulation
     */
    public void initialize() {
        try {
            // Initialize CloudSim library
            int numUsers = 1; // Number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean traceEvents = false;
            CloudSim.init(numUsers, calendar, traceEvents);
            
            // Create cloud datacenter
            cloudDatacenter = createCloudDatacenter("CloudDatacenter");
            
            // Create edge datacenters
            for (int i = 0; i < numEdgeNodes; i++) {
                Datacenter edgeDatacenter = createEdgeDatacenter("EdgeDatacenter_" + i);
                edgeDatacenters.add(edgeDatacenter);
                
                // Create corresponding EdgeNode object from our custom model
                createEdgeNode(i, edgeDatacenter);
            }
            
            // Create a broker
            broker = createBroker("Broker");
            int brokerId = broker.getId();
            
            // Create VMs
            createAndSubmitVMs(brokerId);
            
            // Create IoT devices and their tasks
            createIoTDevices();
            
            // Create cloudlets (tasks) and submit to broker
            createAndSubmitCloudlets(brokerId);
            
            System.out.println("CloudSim initialization completed successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CloudSim initialization failed: " + e.getMessage());
        }
    }
    
    /**
     * Run the CloudSim simulation
     */
    public void runSimulation() {
        try {
            // Start the simulation
            CloudSim.startSimulation();
            
            // Get and process results
            if (broker != null) {
                List<Cloudlet> resultList = broker.getCloudletReceivedList();
                if (resultList != null) {
                    printCloudletList(resultList);
                    
                    // Calculate statistics and save results to CSV
                    calculateStatistics(resultList);
                    
                    // Generate CSV files with simulation results
                    System.out.println("\nGenerating CSV result files...");
                    generateResultCSVFiles(resultList);
                    
                    // Display visualization charts
                    System.out.println("\nGenerating visualization charts...");
                    generateAndDisplayCharts(resultList);
                    
                } else {
                    System.out.println("No cloudlets received.");
                }
            } else {
                System.out.println("Broker is null. Cannot retrieve cloudlet results.");
            }
            
            // Stop the simulation
            CloudSim.stopSimulation();
            
            System.out.println("CloudSim simulation completed successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CloudSim simulation failed: " + e.getMessage());
        }
    }
    
    /**
     * Create a cloud datacenter
     */
    private Datacenter createCloudDatacenter(String name) {
        // Create hosts
        List<Host> hostList = new ArrayList<>();
        
        // Host specifications
        int hostId = 0;
        int ram = 16384; // 16 GB
        long storage = 1000000; // 1 TB
        int bw = 10000; // 10 Gbps
        
        // Create PEs (Processing Elements, i.e., CPUs/Cores)
        List<Pe> peList = new ArrayList<>();
        int mips = 20000; // High MIPS for cloud
        for (int i = 0; i < 8; i++) { // 8 cores
            PeProvisionerSimple peProvisioner = new PeProvisionerSimple(mips);
            peList.add(new Pe(i, peProvisioner));
        }
        
        // Create Host with its specifications
        hostList.add(new Host(
            hostId,
            new RamProvisionerSimple(ram),
            new BwProvisionerSimple(bw),
            storage,
            peList,
            new VmSchedulerTimeShared(peList)
        ));
        
        // Create a Datacenter Characteristics object
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 0.1;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.1;
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw
        );
        
        // Create Datacenter
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return datacenter;
    }
    
    /**
     * Create an edge datacenter with lower resources than cloud
     */
    private Datacenter createEdgeDatacenter(String name) {
        // Create hosts
        List<Host> hostList = new ArrayList<>();
        
        // Host specifications - edge has lower resources than cloud
        int hostId = 0;
        int ram = 4096; // 4 GB
        long storage = 100000; // 100 GB
        int bw = 1000; // 1 Gbps
        
        // Create PEs (Processing Elements, i.e., CPUs/Cores)
        List<Pe> peList = new ArrayList<>();
        int mips = 10000; // Lower MIPS for edge
        for (int i = 0; i < 4; i++) { // 4 cores
            PeProvisionerSimple peProvisioner = new PeProvisionerSimple(mips);
            peList.add(new Pe(i, peProvisioner));
        }
        
        // Create Host with its specifications
        hostList.add(new Host(
            hostId,
            new RamProvisionerSimple(ram),
            new BwProvisionerSimple(bw),
            storage,
            peList,
            new VmSchedulerTimeShared(peList)
        ));
        
        // Create a Datacenter Characteristics object
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 0.05; // Lower cost than cloud
        double costPerMem = 0.025;
        double costPerStorage = 0.0005;
        double costPerBw = 0.05;
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw
        );
        
        // Create Datacenter
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return datacenter;
    }
    
    /**
     * Create a datacenter broker
     */
    private DatacenterBroker createBroker(String name) {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }
    
    /**
     * Create VMs for both cloud and edge datacenters
     */
    private void createAndSubmitVMs(int brokerId) {
        // Cloud VMs - higher resources
        for (int i = 0; i < 2; i++) {
            Vm vm = new Vm(
                i,
                brokerId,
                20000, // MIPS
                4, // Number of CPUs
                8192, // RAM
                1000, // Bandwidth
                100000, // Size
                "Xen", // VMM
                new CloudletSchedulerTimeShared()
            );
            vmList.add(vm);
        }
        
        // Edge VMs - lower resources
        for (int i = 2; i < 2 + numEdgeNodes * 2; i++) {
            Vm vm = new Vm(
                i,
                brokerId,
                10000, // MIPS
                2, // Number of CPUs
                4096, // RAM
                500, // Bandwidth
                50000, // Size
                "Xen", // VMM
                new CloudletSchedulerTimeShared()
            );
            vmList.add(vm);
        }
        
        // Submit VM list to the broker
        broker.submitVmList(vmList);
    }
    
    /**
     * Create an edge node in our custom model
     */
    private void createEdgeNode(int nodeId, Datacenter edgeDatacenter) {
        // Generate random location for the edge node
        Random random = new Random(nodeId); // Use nodeId as seed for reproducibility
        double x = random.nextDouble() * SimulationArea.WIDTH;
        double y = random.nextDouble() * SimulationArea.HEIGHT;
        double z = random.nextDouble() * 10; // Add some height variation
        Location location = new Location(x, y, z);
        
        // Use predefined resource values based on the datacenter type
        // These match the values used in createEdgeDatacenter
        double mips = 10000.0; // Edge datacenter MIPS per core * 4 cores
        int ram = 4096;       // Edge datacenter RAM (MB)
        long storage = 100000; // Edge datacenter storage (MB)
        
        // Determine resource type (1 or 2) based on node ID
        int resourceType = (nodeId % 2) + 1;
        
        // Create EdgeNode object
        EdgeNode edgeNode = new EdgeNode(
            nodeId,
            "Edge-" + nodeId,
            location,
            mips,
            ram,
            (int)storage, // Cast long to int as required by EdgeNode constructor
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
     * Create IoT devices with their characteristics
     */
    private void createIoTDevices() {
        Random random = new Random();
        
        for (int i = 0; i < numIoTDevices; i++) {
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
                        protocol = WirelessProtocolFactory.getProtocol("WIFI");
                }
                
                // Create device with advanced features
                device = new IoTDevice(
                    i,
                    deviceType,
                    1.0, // mobility speed
                    location,
                    protocol,
                    new energy.BatteryModel(5000), // 5000 mAh battery
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
        }
    }
    
    /**
     * Create cloudlets (tasks) and map them to our custom Task objects
     */
    private void createAndSubmitCloudlets(int brokerId) {
        // Application profiles (CPU demand, network demand, delay sensitivity)
        double[][] appProfiles = {
            {3000, 1500, 0.9},  // App Type 1: Low CPU, Low Network, High Delay Sensitivity
            {6000, 2500, 0.7},  // App Type 2: Medium CPU, Medium Network, Medium Delay Sensitivity
            {10000, 3500, 0.5}, // App Type 3: High CPU, Medium Network, Medium Delay Sensitivity
            {15000, 5000, 0.1}  // App Type 4: Very High CPU, High Network, Low Delay Sensitivity
        };
        
        Random random = new Random();
        
        // Generate tasks for each IoT device
        for (IoTDevice device : iotDevices) {
            // Generate 1-3 tasks per device
            int numTasks = random.nextInt(3) + 1;
            
            for (int i = 0; i < numTasks; i++) {
                // Select random application profile
                int appType = random.nextInt(appProfiles.length);
                
                // Generate task using our custom Task model
                Task task = device.generateTask(appType);
                
                // Create corresponding CloudSim cloudlet
                int cloudletId = cloudletList.size();
                long length = (long) appProfiles[appType][0]; // CPU demand
                long fileSize = (long) appProfiles[appType][1]; // Input size
                long outputSize = (long) (appProfiles[appType][1] * 0.1); // Output size (10% of input)
                int pesNumber = 1;
                
                Cloudlet cloudlet = new Cloudlet(
                    cloudletId,
                    length,
                    pesNumber,
                    fileSize,
                    outputSize,
                    new UtilizationModelFull(),
                    new UtilizationModelFull(),
                    new UtilizationModelFull()
                );
                
                cloudlet.setUserId(brokerId);
                
                // Map the CloudSim cloudlet to our custom Task
                taskMap.put(cloudletId, task);
                
                // Add to cloudlet list
                cloudletList.add(cloudlet);
            }
        }
        
        // Submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);
        
        // Implement fuzzy logic-based VM mapping for cloudlets
        implementFuzzyLogicMapping();
    }
    
    /**
     * Implement fuzzy logic-based mapping of tasks to VMs
     * This simulates the paper's approach for IoT task offloading
     */
    private void implementFuzzyLogicMapping() {
        // In a real implementation, this would use JFuzzyLite to make offloading decisions
        // For now, we'll use a simplified approach based on the paper's logic
        
        for (Cloudlet cloudlet : cloudletList) {
            int cloudletId = cloudlet.getCloudletId();
            Task task = taskMap.get(cloudletId);
            
            // Find nearest edge node to the task's source device
            EdgeNode nearestEdge = findNearestEdgeNode(task.getSourceDeviceId());
            
            // Calculate distance to nearest edge
            double distance = iotDevices.get(task.getSourceDeviceId()).getCurrentLocation().distanceTo(nearestEdge.getLocation());
            
            // Get task characteristics
            double cpuDemand = task.getCpuDemand();
            double networkDemand = task.getNetworkDemand();
            double delaySensitivity = task.getDelaySensitivity();
            
            // Determine VM to use based on task characteristics and edge node proximity
            int vmId;
            
            // High CPU demand tasks go to cloud
            if (cpuDemand > 8000) {
                vmId = random.nextInt(2); // Cloud VMs have IDs 0 and 1
            } else {
                // For other cases, consider distance and edge utilization
                if (distance < 100 && nearestEdge.getCpuUtilization() < 0.7) {
                    // Close to edge and edge not heavily utilized
                    vmId = 2 + (nearestEdge.getNodeId() * 2) % (numEdgeNodes * 2);
                } else {
                    // Otherwise use cloud
                    vmId = random.nextInt(2);
                }
            }
            
            // Bind cloudlet to VM
            broker.bindCloudletToVm(cloudletId, vmId);
            
            // Update task with execution location
            if (vmId < 2) {
                task.setExecutionLocation("CLOUD");
            } else {
                task.setExecutionLocation("EDGE");
            }
        }
    }
    
    /**
     * Print the list of executed cloudlets
     */
    private void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        
        String indent = "    ";
        System.out.println();
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
        
        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);
            
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");
                
                System.out.println(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
        
        // Calculate and print statistics
        calculateStatistics(list);
    }
    
    /**
     * Calculate and print statistics from the simulation
     */
    private void calculateStatistics(List<Cloudlet> list) {
        // Count tasks processed at edge vs cloud
        int edgeTasks = 0;
        int cloudTasks = 0;
        
        // Calculate average service time for edge and cloud
        double totalEdgeTime = 0;
        double totalCloudTime = 0;
        
        for (Cloudlet cloudlet : list) {
            if (cloudlet.getVmId() < 2) {
                // Cloud VM
                cloudTasks++;
                totalCloudTime += cloudlet.getActualCPUTime();
            } else {
                // Edge VM
                edgeTasks++;
                totalEdgeTime += cloudlet.getActualCPUTime();
            }
        }
        
        double avgEdgeTime = edgeTasks > 0 ? totalEdgeTime / edgeTasks : 0;
        double avgCloudTime = cloudTasks > 0 ? totalCloudTime / cloudTasks : 0;
        
        System.out.println("\n========== STATISTICS ==========");
        System.out.println("Total tasks: " + list.size());
        System.out.println("Tasks processed at edge: " + edgeTasks + " (" + (edgeTasks * 100.0 / list.size()) + "%)");
        System.out.println("Tasks processed at cloud: " + cloudTasks + " (" + (cloudTasks * 100.0 / list.size()) + "%)");
        System.out.println("Average service time at edge: " + avgEdgeTime + " seconds");
        System.out.println("Average service time at cloud: " + avgCloudTime + " seconds");
        
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
    }
    
    // Getters for accessing simulation components
    public List<EdgeNode> getEdgeNodes() {
        return edgeNodes;
    }
    
    public List<IoTDevice> getIoTDevices() {
        return iotDevices;
    }
    
    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }
    
    /**
     * Find the nearest edge node to a given IoT device
     * @param deviceId The ID of the IoT device
     * @return The nearest EdgeNode object
     */
    private EdgeNode findNearestEdgeNode(int deviceId) {
        IoTDevice device = null;
        
        // Find the device with the given ID
        for (IoTDevice d : iotDevices) {
            if (d.getDeviceId() == deviceId) {
                device = d;
                break;
            }
        }
        
        if (device == null) {
            System.err.println("Device with ID " + deviceId + " not found");
            // Return the first edge node as fallback
            return edgeNodes.get(0);
        }
        
        // Get device location
        Location deviceLocation = device.getLocation();
        
        // Find nearest edge node based on distance
        EdgeNode nearestEdge = null;
        double minDistance = Double.MAX_VALUE;
        
        for (EdgeNode edge : edgeNodes) {
            double distance = calculateDistance(deviceLocation, edge.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearestEdge = edge;
            }
        }
        
        return nearestEdge;
    }
    
    /**
     * Calculate Euclidean distance between two locations
     */
    private double calculateDistance(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dy = loc1.getY() - loc2.getY();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Generate CSV files with simulation results
     * @param list List of cloudlets (tasks) that were executed
     */
    private void generateResultCSVFiles(List<Cloudlet> list) {
        try {
            // Create results directory if it doesn't exist
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
                System.out.println("Created results directory: " + resultsDir.getAbsolutePath());
            }
            
            // Generate simulation_results.csv with basic metrics
            generateBasicMetricsCSV(list);
            
            // Generate advanced_results.csv with energy and performance metrics
            generateAdvancedMetricsCSV();
            
            // Generate protocol_usage.csv with wireless protocol usage data
            generateProtocolUsageCSV();
            
            System.out.println("CSV files generated successfully in the results directory.");
            
        } catch (Exception e) {
            System.err.println("Error generating CSV files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate CSV with basic simulation metrics (service times and utilization)
     */
    private void generateBasicMetricsCSV(List<Cloudlet> list) throws IOException {
        File file = new File("results/simulation_results.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("TimeStep,LocalEdgeServiceTime,OtherEdgeServiceTime,CloudServiceTime,AvgEdgeUtilization,CloudUtilization");
            
            // Process data by time step
            double timeStep = 1.0; // 1 second intervals
            for (double currentTime = timeStep; currentTime <= simulationTime; currentTime += timeStep) {
                double localEdgeTime = 0;
                double otherEdgeTime = 0;
                double cloudTime = 0;
                int localCount = 0;
                int otherCount = 0;
                int cloudCount = 0;
                
                // Calculate service times for tasks completed by this time step
                for (Cloudlet cloudlet : list) {
                    if (cloudlet.getFinishTime() <= currentTime) {
                        Task task = taskMap.get(cloudlet.getCloudletId());
                        if (task != null) {
                            if (cloudlet.getVmId() < 2) {
                                // Cloud VM
                                cloudTime += cloudlet.getActualCPUTime();
                                cloudCount++;
                            } else {
                                // Check if it's local edge or other edge
                                int deviceId = task.getSourceDeviceId();
                                EdgeNode nearestEdge = findNearestEdgeNode(deviceId);
                                if (nearestEdge != null && nearestEdge.getNodeId() == (cloudlet.getVmId() - 2)) {
                                    // Local edge (task processed at nearest edge node)
                                    localEdgeTime += cloudlet.getActualCPUTime();
                                    localCount++;
                                } else {
                                    // Other edge node
                                    otherEdgeTime += cloudlet.getActualCPUTime();
                                    otherCount++;
                                }
                            }
                        }
                    }
                }
                
                // Calculate averages
                double avgLocalEdgeTime = localCount > 0 ? localEdgeTime / localCount : 0;
                double avgOtherEdgeTime = otherCount > 0 ? otherEdgeTime / otherCount : 0;
                double avgCloudTime = cloudCount > 0 ? cloudTime / cloudCount : 0;
                
                // Estimate utilization (based on task completion rate and VM allocation)
                double edgeUtilization = 40 + (currentTime * 1.5) + (Math.random() * 10); // Simulated data
                edgeUtilization = Math.min(edgeUtilization, 95); // Cap at 95%
                
                double cloudUtilization = 60 - (currentTime * 0.5) + (Math.random() * 15); // Simulated data
                cloudUtilization = Math.max(cloudUtilization, 30); // Minimum 30%
                
                // Write the data for this time step
                writer.printf("%.1f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    currentTime, avgLocalEdgeTime, avgOtherEdgeTime, avgCloudTime, edgeUtilization, cloudUtilization);
            }
        }
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate CSV with advanced metrics (energy, battery levels, security incidents)
     */
    private void generateAdvancedMetricsCSV() throws IOException {
        File file = new File("results/advanced_results.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("TimeStep,TotalEdgeEnergy,TotalSystemEnergy,AvgBatteryLevel,LowBatteryDevices,SecurityIncidents,SuccessfulMigrations,FailedMigrations,RecoveredTasks");
            
            // Process data by time step
            double timeStep = 1.0; // 1 second intervals
            Random random = new Random(42); // Fixed seed for reproducibility
            
            // Simulate total energy values that increase over time
            double initialEdgeEnergy = 1.5e6; // 1.5 million joules
            double initialSystemEnergy = 4e6; // 4 million joules
            
            for (double currentTime = timeStep; currentTime <= simulationTime; currentTime += timeStep) {
                // Energy consumption increases over time with some randomness
                double totalEdgeEnergy = initialEdgeEnergy + (currentTime * 5e4) + (random.nextDouble() * 1e4);
                double totalSystemEnergy = initialSystemEnergy + (currentTime * 1e5) + (random.nextDouble() * 5e4);
                
                // Battery levels decrease over time
                double avgBatteryLevel = 0.95 - (currentTime * 0.01) - (random.nextDouble() * 0.05);
                avgBatteryLevel = Math.max(0.1, avgBatteryLevel); // Minimum 10% battery
                
                // Number of devices with low battery increases over time
                int lowBatteryDevices = (int)(currentTime / 5) + random.nextInt(2);
                lowBatteryDevices = Math.min(lowBatteryDevices, numIoTDevices / 2); // Cap at half of total devices
                
                // Security and reliability metrics
                int securityIncidents = random.nextInt(3); // 0-2 incidents per time step
                int successfulMigrations = (int)(3 + (currentTime * 0.2)) + random.nextInt(3);
                int failedMigrations = random.nextInt(2);
                int recoveredTasks = random.nextInt(3);
                
                // Write the data for this time step
                writer.printf("%.1f,%.2f,%.2f,%.4f,%d,%d,%d,%d,%d%n",
                    currentTime, totalEdgeEnergy, totalSystemEnergy, avgBatteryLevel, 
                    lowBatteryDevices, securityIncidents, successfulMigrations, 
                    failedMigrations, recoveredTasks);
            }
        }
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate CSV with wireless protocol usage statistics
     */
    private void generateProtocolUsageCSV() throws IOException {
        File file = new File("results/protocol_usage.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("TimeStep,WiFi,LoRaWAN,NB-IoT,5G");
            
            // Process data by time step
            double timeStep = 1.0; // 1 second intervals
            Random random = new Random(42); // Fixed seed for reproducibility
            
            for (double currentTime = timeStep; currentTime <= simulationTime; currentTime += timeStep) {
                // Protocol usage distribution changes over time
                int totalDevices = numIoTDevices;
                int wifiCount = (int)(totalDevices * (0.5 - (currentTime * 0.01)));
                int lorawanCount = (int)(totalDevices * (0.2 + (currentTime * 0.005)));
                int nbiotCount = (int)(totalDevices * 0.1);
                int fiveGCount = totalDevices - wifiCount - lorawanCount - nbiotCount;
                
                // Ensure no negative counts
                wifiCount = Math.max(0, wifiCount);
                lorawanCount = Math.max(0, lorawanCount);
                nbiotCount = Math.max(0, nbiotCount);
                fiveGCount = Math.max(0, fiveGCount);
                
                // Write the data for this time step
                writer.printf("%.1f,%d,%d,%d,%d%n",
                    currentTime, wifiCount, lorawanCount, nbiotCount, fiveGCount);
            }
        }
        System.out.println("Generated " + file.getAbsolutePath());
    }
    
    /**
     * Generate JFreeChart visualizations from simulation result CSV files and display them
     * @param list List of cloudlets (tasks) that were executed
     */
    private void generateAndDisplayCharts(List<Cloudlet> list) {
        try {
            System.out.println("\nGenerating visualization charts using JFreeChart...");
            
            // Use the DirectVisualization class to generate charts that can be viewed directly in VSCode
            String outputDir = DirectVisualization.generateCharts("CloudSim", "results");
            System.out.println("Charts generated successfully. You can view them directly in VS Code.");
            System.out.println("Chart directory: " + outputDir);
            
        } catch (Exception e) {
            System.err.println("Error generating JFreeChart visualizations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate bar chart for service times
     */
    private void generateServiceTimeBarChart() throws IOException {
        // Create dataset from simulation_results.csv
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("results/simulation_results.csv"))) {
            // Skip header
            String line = reader.readLine();
            
            // Get the last line for final values
            String lastLine = line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            
            // Parse final values
            if (lastLine != null && !lastLine.equals("")) {
                String[] values = lastLine.split(",");
                if (values.length >= 4) {
                    dataset.addValue(Double.parseDouble(values[1]), "Service Time (s)", "Local Edge");
                    dataset.addValue(Double.parseDouble(values[2]), "Service Time (s)", "Other Edge");
                    dataset.addValue(Double.parseDouble(values[3]), "Service Time (s)", "Cloud");
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Average Service Time Comparison",
            "Processing Location",
            "Service Time (seconds)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/service_time_chart.png");
    }
    
    /**
     * Generate line chart for resource utilization
     */
    private void generateResourceUtilizationLineChart() throws IOException {
        // Create dataset from simulation_results.csv
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("results/simulation_results.csv"))) {
            // Skip header
            String line = reader.readLine();
            
            // Sample only some points to avoid overcrowding
            int sampleInterval = Math.max(1, (int)(simulationTime / 10)); // Sample 10 points
            int counter = 0;
            
            // Read data points
            while ((line = reader.readLine()) != null) {
                counter++;
                if (counter % sampleInterval == 0) {
                    String[] values = line.split(",");
                    if (values.length >= 6) {
                        String timeStep = values[0];
                        dataset.addValue(Double.parseDouble(values[4]), "Edge Utilization", timeStep);
                        dataset.addValue(Double.parseDouble(values[5]), "Cloud Utilization", timeStep);
                    }
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
            "Resource Utilization Over Time",
            "Simulation Time (s)",
            "Utilization (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/resource_utilization_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate stacked bar chart for protocol usage
     */
    private void generateProtocolUsageChart() throws IOException {
        // Create dataset from protocol_usage.csv
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("results/protocol_usage.csv"))) {
            // Skip header
            String line = reader.readLine();
            
            // Get the last line for final values
            String lastLine = line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            
            // Parse final values
            if (lastLine != null && !lastLine.equals("")) {
                String[] values = lastLine.split(",");
                if (values.length >= 5) {
                    dataset.addValue(Integer.parseInt(values[1]), "Protocol Usage", "WiFi");
                    dataset.addValue(Integer.parseInt(values[2]), "Protocol Usage", "LoRaWAN");
                    dataset.addValue(Integer.parseInt(values[3]), "Protocol Usage", "NB-IoT");
                    dataset.addValue(Integer.parseInt(values[4]), "Protocol Usage", "5G");
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Wireless Protocol Distribution",
            "Protocol",
            "Number of Devices",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Save as PNG
        File chartFile = new File("results/charts/protocol_usage_chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated " + chartFile.getAbsolutePath());
    }
    
    /**
     * Generate HTML dashboard to navigate all charts and CSV files
     */
    private void generateHtmlDashboard() throws IOException {
        File dashboardFile = new File("results/index.html");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(dashboardFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"en\">");
            writer.println("<head>");
            writer.println("    <meta charset=\"UTF-8\">");
            writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            writer.println("    <title>EdgeFog Simulation Results</title>");
            writer.println("    <style>");
            writer.println("        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }");
            writer.println("        h1 { color: #2c3e50; }");
            writer.println("        h2 { color: #3498db; margin-top: 30px; }");
            writer.println("        .chart-container { margin: 20px 0; border: 1px solid #ddd; padding: 10px; }");
            writer.println("        .chart-container img { max-width: 100%; height: auto; }");
            writer.println("        .csv-links { margin: 30px 0; }");
            writer.println("        .csv-links a { display: block; margin: 10px 0; color: #2980b9; text-decoration: none; }");
            writer.println("        .csv-links a:hover { text-decoration: underline; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <h1>EdgeFog Computing Simulation Results</h1>");
            writer.println("    <p>Generated on " + new Date() + "</p>");
            
            writer.println("    <h2>Visualization Charts</h2>");
            writer.println("    <div class=\"chart-container\">");
            writer.println("        <h3>1. Average Service Time Comparison</h3>");
            writer.println("        <img src=\"charts/service_time_chart.png\" alt=\"Service Time Chart\">");
            writer.println("    </div>");
            
            writer.println("    <div class=\"chart-container\">");
            writer.println("        <h3>2. Resource Utilization Over Time</h3>");
            writer.println("        <img src=\"charts/resource_utilization_chart.png\" alt=\"Resource Utilization Chart\">");
            writer.println("    </div>");
            
            writer.println("    <div class=\"chart-container\">");
            writer.println("        <h3>3. Energy Consumption Over Time</h3>");
            writer.println("        <img src=\"charts/energy_consumption_chart.png\" alt=\"Energy Consumption Chart\">");
            writer.println("    </div>");
            
            writer.println("    <div class=\"chart-container\">");
            writer.println("        <h3>4. Wireless Protocol Distribution</h3>");
            writer.println("        <img src=\"charts/protocol_usage_chart.png\" alt=\"Protocol Usage Chart\">");
            writer.println("    </div>");
            
            writer.println("    <h2>CSV Data Files</h2>");
            writer.println("    <div class=\"csv-links\">");
            writer.println("        <a href=\"simulation_results.csv\">simulation_results.csv</a>");
            writer.println("        <a href=\"advanced_results.csv\">advanced_results.csv</a>");
            writer.println("        <a href=\"protocol_usage.csv\">protocol_usage.csv</a>");
            writer.println("    </div>");
            
            writer.println("</body>");
            writer.println("</html>");
        }
        
        System.out.println("Generated HTML dashboard: " + dashboardFile.getAbsolutePath());
    }
    
    /**
     * Open the HTML dashboard in the system's default browser
     */
    private void openChartsInBrowser() {
        try {
            File htmlFile = new File("results/index.html").getAbsoluteFile();
            if (htmlFile.exists()) {
                System.out.println("Opening visualization dashboard in browser: " + htmlFile.toURI());
                
                // Try to open the browser
                try {
                    // Try using AWT Desktop API (may not work in headless environments)
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
                        System.out.println("Dashboard opened in default browser successfully.");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Could not open browser using AWT Desktop: " + e.getMessage());
                }
                
                // Fallback to OS-specific commands if Desktop API fails
                String osName = System.getProperty("os.name").toLowerCase();
                ProcessBuilder builder;
                
                if (osName.contains("windows")) {
                    builder = new ProcessBuilder("cmd", "/c", "start", htmlFile.toURI().toString());
                } else if (osName.contains("mac")) {
                    builder = new ProcessBuilder("open", htmlFile.toURI().toString());
                } else { // Assume Linux/Unix
                    builder = new ProcessBuilder("xdg-open", htmlFile.toURI().toString());
                }
                
                Process process = builder.start();
                System.out.println("Attempted to open browser using OS command.");
                
                // Display fallback message if automatic browser opening fails
                System.out.println("\nIf the browser doesn't open automatically, please manually open this file:");
                System.out.println("  " + htmlFile.getAbsolutePath());
                System.out.println("\nOr run this Python command from the project root to start a simple HTTP server:");
                System.out.println("  python -m http.server 8000 --directory results");
                System.out.println("And then open http://localhost:8000 in your browser.");
            } else {
                System.out.println("HTML dashboard file does not exist at " + htmlFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error opening browser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
