package test;

import models.EdgeNode;
import models.Location;
import models.Task;
import bigdata.DataProcessor;
import bigdata.StreamingAnalytics;
import bigdata.DataAggregator;
import energy.LinearEnergyModel;

/**
 * Simple test for EdgeNode functionality
 */
public class SimpleEdgeNodeTest {
    
    public static void main(String[] args) {
        System.out.println("Testing EdgeNode functionality...");
        
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
            
            // Test analytics components
            System.out.println("\nTesting analytics components...");
            
            // Create a task
            Task task = new Task(
                "task-1",
                1001,
                0.5, // CPU demand
                512, // Network demand
                0.6, // Delay sensitivity
                System.currentTimeMillis()
            );
            
            // Queue and process the task
            edgeNode.queueTask(task);
            Task processedTask = edgeNode.processNextTask(System.currentTimeMillis());
            
            // Get analytics results
            System.out.println("Analytics processors: " + edgeNode.getAnalyticsResults());
            
            System.out.println("\nAll tests completed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
