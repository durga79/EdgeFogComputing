#!/bin/bash

echo "Edge-Fog Computing Task Offloading System - Component Tester"
echo "=========================================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java first."
    echo "You can run: sudo apt update && sudo apt install -y openjdk-11-jdk"
    exit 1
fi

# Create bin directory for compiled classes
mkdir -p bin

# Function to compile and test a specific component
test_component() {
    local component=$1
    local main_class=$2
    
    echo "Testing $component..."
    
    # Compile the component and its dependencies
    echo "Compiling $component..."
    javac -d bin src/models/*.java src/fuzzy_logic/*.java src/$component.java
    
    if [ $? -ne 0 ]; then
        echo "Failed to compile $component. Please check the error messages above."
        return 1
    fi
    
    # Run the component's test
    echo "Running $component test..."
    java -cp bin $main_class
    
    if [ $? -ne 0 ]; then
        echo "Test failed for $component. Please check the error messages above."
        return 1
    fi
    
    echo "$component test completed successfully!"
    return 0
}

# Menu for selecting which component to test
echo "Select a component to test:"
echo "1. Fuzzy Logic Controller"
echo "2. IoT Device"
echo "3. Edge Node"
echo "4. Cloud"
echo "5. Edge Controller"
echo "6. Full Simulation"
echo "7. Exit"

read -p "Enter your choice (1-7): " choice

case $choice in
    1)
        # Create a simple test for FuzzyLogicController
        cat > src/fuzzy_logic/TestFuzzyLogic.java << EOF
package fuzzy_logic;

public class TestFuzzyLogic {
    public static void main(String[] args) {
        System.out.println("Testing Fuzzy Logic Controller...");
        FuzzyLogicController controller = new FuzzyLogicController();
        
        // Test with different input combinations
        System.out.println("Test Case 1: High CPU, Low Network, High Delay Sensitivity, Low Edge Utilization");
        String decision = controller.makeOffloadingDecision(0.9, 0.2, 0.9, 0.3, 1);
        System.out.println("Decision: " + decision);
        
        System.out.println("Test Case 2: Low CPU, High Network, High Delay Sensitivity, High Edge Utilization");
        decision = controller.makeOffloadingDecision(0.2, 0.9, 0.9, 0.8, 1);
        System.out.println("Decision: " + decision);
        
        System.out.println("Test Case 3: High CPU, High Network, Low Delay Sensitivity, Medium Edge Utilization");
        decision = controller.makeOffloadingDecision(0.9, 0.9, 0.2, 0.5, 1);
        System.out.println("Decision: " + decision);
        
        System.out.println("Fuzzy Logic Controller test completed!");
    }
}
EOF
        test_component "fuzzy_logic/TestFuzzyLogic" "fuzzy_logic.TestFuzzyLogic"
        ;;
    2)
        # Create a simple test for IoTDevice
        cat > src/models/TestIoTDevice.java << EOF
package models;

import java.util.ArrayList;
import java.util.List;

public class TestIoTDevice {
    public static void main(String[] args) {
        System.out.println("Testing IoT Device...");
        
        // Create a simulation area
        SimulationArea area = new SimulationArea(1000, 1000);
        
        // Create an IoT device
        IoTDevice device = new IoTDevice(1, "smartphone", 5.0, new Location(500, 500));
        System.out.println("Created device at location: (" + 
                          device.getCurrentLocation().getX() + ", " + 
                          device.getCurrentLocation().getY() + ")");
        
        // Test task generation
        Task task = device.generateTask(1);
        System.out.println("Generated task: " + task);
        System.out.println("CPU demand: " + task.getCpuDemand());
        System.out.println("Network demand: " + task.getNetworkDemand());
        System.out.println("Delay sensitivity: " + task.getDelaySensitivity());
        
        // Test mobility
        System.out.println("Testing mobility...");
        for (int i = 0; i < 5; i++) {
            device.updateLocation(area);
            System.out.println("New location: (" + 
                              device.getCurrentLocation().getX() + ", " + 
                              device.getCurrentLocation().getY() + ")");
        }
        
        // Test nearest edge node finding
        List<EdgeNode> edgeNodes = new ArrayList<>();
        edgeNodes.add(new EdgeNode(1, "type1", 1000, 2048, 10000, new Location(200, 200)));
        edgeNodes.add(new EdgeNode(2, "type2", 2000, 4096, 20000, new Location(800, 800)));
        
        EdgeNode nearest = device.findNearestEdgeNode(edgeNodes);
        System.out.println("Nearest edge node: " + nearest.getNodeId());
        
        System.out.println("IoT Device test completed!");
    }
}
EOF
        test_component "models/TestIoTDevice" "models.TestIoTDevice"
        ;;
    3)
        # Create a simple test for EdgeNode
        cat > src/models/TestEdgeNode.java << EOF
package models;

public class TestEdgeNode {
    public static void main(String[] args) {
        System.out.println("Testing Edge Node...");
        
        // Create an edge node
        EdgeNode edgeNode = new EdgeNode(1, "type1", 1000, 2048, 10000, new Location(500, 500));
        System.out.println("Created edge node with CPU capacity: " + edgeNode.getCpuCapacity() + " MIPS");
        
        // Create and process tasks
        Task task1 = new Task(1, 1, 500, 1000, 0.8);
        Task task2 = new Task(2, 1, 300, 500, 0.5);
        
        System.out.println("Processing task 1...");
        edgeNode.addTask(task1);
        edgeNode.processTasks(1.0); // Process for 1 second
        System.out.println("Task 1 status: " + task1.getStatus());
        System.out.println("Edge node CPU utilization: " + edgeNode.getCpuUtilization());
        
        System.out.println("Processing task 2...");
        edgeNode.addTask(task2);
        edgeNode.processTasks(2.0); // Process for 2 seconds
        System.out.println("Task 1 status: " + task1.getStatus());
        System.out.println("Task 2 status: " + task2.getStatus());
        System.out.println("Edge node CPU utilization: " + edgeNode.getCpuUtilization());
        
        System.out.println("Edge Node test completed!");
    }
}
EOF
        test_component "models/TestEdgeNode" "models.TestEdgeNode"
        ;;
    4)
        # Create a simple test for Cloud
        cat > src/models/TestCloud.java << EOF
package models;

public class TestCloud {
    public static void main(String[] args) {
        System.out.println("Testing Cloud...");
        
        // Create a cloud instance
        Cloud cloud = new Cloud(10000, 16384, 1000000, 100.0, 1000.0);
        System.out.println("Created cloud with CPU capacity: " + cloud.getCpuCapacity() + " MIPS");
        System.out.println("WAN latency: " + cloud.getWanLatency() + " ms");
        System.out.println("WAN bandwidth: " + cloud.getWanBandwidth() + " Mbps");
        
        // Create and process tasks
        Task task1 = new Task(1, 1, 2000, 3000, 0.3);
        Task task2 = new Task(2, 1, 5000, 2000, 0.2);
        
        // Calculate transfer times
        double transferTime1 = cloud.calculateTransferTime(task1);
        double transferTime2 = cloud.calculateTransferTime(task2);
        System.out.println("Transfer time for task 1: " + transferTime1 + " ms");
        System.out.println("Transfer time for task 2: " + transferTime2 + " ms");
        
        // Process tasks
        System.out.println("Processing tasks in cloud...");
        cloud.addTask(task1);
        cloud.addTask(task2);
        cloud.processTasks(5.0); // Process for 5 seconds
        
        System.out.println("Task 1 status: " + task1.getStatus());
        System.out.println("Task 2 status: " + task2.getStatus());
        System.out.println("Cloud CPU utilization: " + cloud.getCpuUtilization());
        
        System.out.println("Cloud test completed!");
    }
}
EOF
        test_component "models/TestCloud" "models.TestCloud"
        ;;
    5)
        # Create a simple test for EdgeController
        cat > src/models/TestEdgeController.java << EOF
package models;

import fuzzy_logic.FuzzyLogicController;
import java.util.ArrayList;
import java.util.List;

public class TestEdgeController {
    public static void main(String[] args) {
        System.out.println("Testing Edge Controller...");
        
        // Create fuzzy logic controller
        FuzzyLogicController fuzzyController = new FuzzyLogicController();
        
        // Create edge nodes
        List<EdgeNode> edgeNodes = new ArrayList<>();
        edgeNodes.add(new EdgeNode(1, "type1", 1000, 2048, 10000, new Location(200, 200)));
        edgeNodes.add(new EdgeNode(2, "type2", 2000, 4096, 20000, new Location(800, 800)));
        
        // Create cloud
        Cloud cloud = new Cloud(10000, 16384, 1000000, 100.0, 1000.0);
        
        // Create edge controller
        EdgeController controller = new EdgeController(edgeNodes, cloud, fuzzyController);
        
        // Create IoT devices
        List<IoTDevice> devices = new ArrayList<>();
        devices.add(new IoTDevice(1, "smartphone", 5.0, new Location(300, 300)));
        devices.add(new IoTDevice(2, "laptop", 2.0, new Location(700, 700)));
        
        // Register devices to edge nodes
        System.out.println("Registering devices to edge nodes...");
        for (IoTDevice device : devices) {
            controller.registerDevice(device);
        }
        
        // Generate and process tasks
        System.out.println("Generating and processing tasks...");
        for (IoTDevice device : devices) {
            Task task = device.generateTask(1);
            System.out.println("Device " + device.getDeviceId() + " generated task " + task.getTaskId());
            controller.processTask(task, device);
        }
        
        // Run one simulation step
        System.out.println("Running simulation step...");
        controller.processAllTasks(1.0);
        
        // Print statistics
        System.out.println("Edge Controller Statistics:");
        System.out.println("Tasks processed: " + controller.getTotalTasksProcessed());
        System.out.println("Average service time: " + controller.getAverageServiceTime() + " ms");
        
        System.out.println("Edge Controller test completed!");
    }
}
EOF
        test_component "models/TestEdgeController" "models.TestEdgeController"
        ;;
    6)
        echo "Running full simulation..."
        ./run_simulation.sh
        ;;
    7)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice. Exiting."
        exit 1
        ;;
esac
