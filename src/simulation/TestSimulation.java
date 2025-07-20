package simulation;

import models.*;
import services.*;
import security.*;
import network.*;
import energy.*;
import bigdata.*;

/**
 * Simple test class to verify the functionality of the Edge-Fog Computing components
 */
public class TestSimulation {

    public static void main(String[] args) {
        System.out.println("Testing Edge-Fog Computing Components");
        System.out.println("=====================================");
        
        try {
            // Create an edge node
            // Create a location for the edge node
            Location edgeLocation = new Location(100.0, 100.0);
            
            EdgeNode edgeNode = new EdgeNode(
                1, 
                "Edge-1", 
                edgeLocation,
                1000, // MIPS
                2048, // RAM in MB
                10000, // Storage in MB
                1 // Resource type (1 = low resources)
            );
            
            System.out.println("Created EdgeNode: " + edgeNode.getNodeId() + " - " + edgeNode.getNodeName());
            
            // Add supported protocols
            edgeNode.addSupportedProtocol("WIFI");
            edgeNode.addSupportedProtocol("LORAWAN");
            
            System.out.println("Added supported protocols");
            
            // Create an IoT device
            Location deviceLocation = new Location(150.0, 150.0);
            
            IoTDevice device = new IoTDevice(
                1,
                "Device-1",
                5.0, // Mobility speed
                deviceLocation
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
            
            // Note: Task data is generated automatically when the task is processed
            // We'll check for generated data after processing
            
            // Queue and process task
            edgeNode.queueTask(task);
            System.out.println("Task queued. Queue size: " + edgeNode.getQueueSize());
            
            Task processedTask = edgeNode.processNextTask(System.currentTimeMillis());
            if (processedTask != null) {
                System.out.println("Task processed: " + processedTask.getTaskId());
                System.out.println("Task execution time: " + processedTask.getActualExecutionTime() + " ms");
                System.out.println("Task status: " + processedTask.getStatus());
            }
            
            // Test analytics results
            System.out.println("Analytics processors: " + edgeNode.getAnalyticsResults());
            
            System.out.println("\nAll tests completed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
