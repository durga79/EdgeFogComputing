package test;

import models.EdgeNode;
import models.IoTDevice;
import models.Task;
import models.Location;
import services.ServiceDiscovery;
import security.SecurityManager;
import network.WirelessProtocol;
import network.WirelessProtocolFactory;
import energy.EnergyModel;
import energy.LinearEnergyModel;
import energy.BatteryModel;
import bigdata.DataProcessor;
import bigdata.StreamingAnalytics;
import bigdata.DataAggregator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple test class to verify the functionality of the Edge-Fog Computing components
 */
public class TestEdgeFogComponents {

    public static void main(String[] args) {
        System.out.println("Testing Edge-Fog Computing Components");
        System.out.println("=====================================");
        
        // Test EdgeNode and IoT Device creation
        testEdgeNodeAndIoTDevice();
        
        // Test Service Discovery
        testServiceDiscovery();
        
        // Test Security Components
        testSecurityComponents();
        
        // Test Big Data Analytics
        testBigDataAnalytics();
        
        System.out.println("\nAll tests completed successfully!");
    }
    
    private static void testEdgeNodeAndIoTDevice() {
        System.out.println("\nTesting EdgeNode and IoT Device creation...");
        
        // Create an edge node
        EdgeNode edgeNode = new EdgeNode(
            1, 
            "Edge-1", 
            new Location(100.0, 100.0), 
            1000, // MIPS
            2048, // RAM in MB
            10000, // Storage in MB
            1 // Resource type
        );
        
        System.out.println("Created EdgeNode: " + edgeNode.getNodeId() + " - " + edgeNode.getNodeName());
        
        // Create an IoT device
        IoTDevice device = new IoTDevice(
            1,
            "Device-1",
            5.0, // Speed
            new Location(150.0, 150.0)
        );
        
        System.out.println("Created IoT Device: " + device.getDeviceId() + " - " + device.getDeviceType());
        
        // Connect device to edge node
        edgeNode.addConnectedDevice(device);
        System.out.println("Connected devices count: " + edgeNode.getConnectedDevices().size());
        
        // Create and process a task
        Task task = new Task(
            "task-1",
            device.getDeviceId(),
            0.7, // CPU demand
            1024, // Network demand
            0.8, // Delay sensitivity
            System.currentTimeMillis()
        );
        
        edgeNode.queueTask(task);
        System.out.println("Task queued. Queue size: " + edgeNode.getQueueSize());
        
        Task processedTask = edgeNode.processNextTask(System.currentTimeMillis());
        if (processedTask != null) {
            System.out.println("Task processed: " + processedTask.getTaskId());
        }
    }
    
    private static void testServiceDiscovery() {
        System.out.println("\nTesting Service Discovery...");
        
        // Create edge node and service discovery
        EdgeNode edgeNode = new EdgeNode(
            2, 
            "Edge-2", 
            new Location(200.0, 200.0), 
            2000, // MIPS
            4096, // RAM in MB
            20000, // Storage in MB
            2 // Resource type
        );
        
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        
        // Register a service
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", "1.0");
        metadata.put("provider", "EdgeFog Inc.");
        
        boolean registered = serviceDiscovery.registerService(
            "service-1",
            "Computation Service",
            "COMPUTATION",
            edgeNode,
            metadata
        );
        
        System.out.println("Service registered: " + registered);
        
        // Find services
        List<ServiceDiscovery.ServiceRegistration> services = serviceDiscovery.findServicesByType("COMPUTATION");
        System.out.println("Found services: " + services.size());
        
        if (!services.isEmpty()) {
            System.out.println("Service details: " + services.get(0));
        }
    }
    
    private static void testSecurityComponents() {
        System.out.println("\nTesting Security Components...");
        
        // Create security manager
        SecurityManager securityManager = new SecurityManager();
        
        // Register a user
        String userId = "user1001";
        String password = "password123";
        boolean registered = securityManager.registerUser(userId, password, SecurityManager.UserRole.ADMIN);
        System.out.println("User registered: " + registered);
        
        // Authenticate and get token
        String token = securityManager.authenticate(userId, password);
        System.out.println("User authenticated with token: " + token);
        
        // Validate token
        boolean valid = securityManager.validateToken(token);
        System.out.println("Token validation: " + valid);
        
        // Create a task
        Task task = new Task(
            "secure-task-1",
            1001,
            0.5, // CPU demand
            512, // Network demand
            0.6, // Delay sensitivity
            System.currentTimeMillis()
        );
        
        // Add security token
        String deviceToken = securityManager.generateToken(1001);
        task.setSecurityToken(deviceToken);
        System.out.println("Secure task created with token: " + task.getSecurityToken());
        
        // Add security metadata
        task.addMetadata("classification", "confidential");
        task.addMetadata("encryption", "AES-256");
        
        System.out.println("Task has security metadata: " + task.hasSecurityMetadata());
    }
    
    private static void testBigDataAnalytics() {
        System.out.println("\nTesting Big Data Analytics...");
        
        // Create data processors
        DataProcessor streamingAnalytics = new StreamingAnalytics(100);
        DataProcessor dataAggregator = new DataAggregator(60);
        
        System.out.println("Created StreamingAnalytics: " + streamingAnalytics.getName());
        System.out.println("Created DataAggregator: " + dataAggregator.getName());
        
        // Create sample data
        Map<String, Object> dataPoint = new HashMap<>();
        dataPoint.put("temperature", 25.5);
        dataPoint.put("humidity", 60.2);
        dataPoint.put("pressure", 1013.25);
        dataPoint.put("timestamp", System.currentTimeMillis());
        
        // Process data
        Map<String, Object> streamingResult = streamingAnalytics.processDataPoint(dataPoint);
        Map<String, Object> aggregationResult = dataAggregator.processDataPoint(dataPoint);
        
        System.out.println("Streaming analytics complexity: " + streamingAnalytics.getComputationalComplexity() + " MI");
        System.out.println("Data aggregator complexity: " + dataAggregator.getComputationalComplexity() + " MI");
    }
}
