package cloudsim_integration;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

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
}
