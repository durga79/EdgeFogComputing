#!/bin/bash

# Test script for the FuzzyLogicController component
# This script compiles and runs a simple test for the fuzzy logic controller

echo "Edge-Fog Computing - FuzzyLogicController Test"
echo "=============================================="

# Create test directory if it doesn't exist
mkdir -p test_bin

# Use Maven-compiled classes
echo "Using Maven-compiled classes..."
MVN_CLASSES="target/classes"
MVN_DEPS="target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Check if Maven build exists
if [ ! -d "$MVN_CLASSES" ] || [ ! -f "$MVN_DEPS" ]; then
    echo "Maven build not found. Running mvn compile..."
    mvn compile assembly:single
fi

# Create a simple test class
cat > TestFuzzyLogic.java << 'EOL'
import fuzzy_logic.FuzzyLogicController;

public class TestFuzzyLogic {
    public static void main(String[] args) {
        System.out.println("Testing FuzzyLogicController...");
        
        // Create a fuzzy logic controller
        FuzzyLogicController controller = new FuzzyLogicController();
        
        // Test different scenarios
        testScenario(controller, "Low CPU, Low Edge Utilization", 
                    2500, 1000, 0.3, 20, 1);
        
        testScenario(controller, "High CPU, High Edge Utilization, Low Delay Sensitivity", 
                    16000, 4000, 0.1, 95, 1);
        
        testScenario(controller, "Medium CPU, High Edge Utilization, High Delay Sensitivity", 
                    7000, 2000, 0.95, 85, 1);
        
        testScenario(controller, "High Network, High Delay Sensitivity", 
                    5000, 4500, 0.95, 50, 1);
        
        testScenario(controller, "High CPU, Low Edge Utilization, High Resource Type", 
                    12000, 3000, 0.5, 25, 2);
        
        testScenario(controller, "High CPU, Medium Edge Utilization, Low Resource Type", 
                    12000, 3000, 0.5, 60, 1);
    }
    
    private static void testScenario(FuzzyLogicController controller, String scenarioName,
                                   double cpuDemand, double networkDemand, 
                                   double delaySensitivity, double edgeUtilization, 
                                   int resourceType) {
        String decision = controller.makeOffloadingDecision(
            cpuDemand, networkDemand, delaySensitivity, edgeUtilization, resourceType);
        
        System.out.println("\nScenario: " + scenarioName);
        System.out.println("  CPU Demand: " + cpuDemand + " MI");
        System.out.println("  Network Demand: " + networkDemand + " KB");
        System.out.println("  Delay Sensitivity: " + delaySensitivity);
        System.out.println("  Edge Utilization: " + edgeUtilization + "%");
        System.out.println("  Resource Type: " + (resourceType == 1 ? "Low" : "High"));
        System.out.println("  Decision: " + decision);
    }
}
EOL

# Compile the test class
javac -cp "$MVN_DEPS:src" -d test_bin TestFuzzyLogic.java

if [ $? -eq 0 ]; then
    echo -e "\nCompilation successful. Running test..."
    java -cp "test_bin:$MVN_DEPS:src" TestFuzzyLogic
else
    echo -e "\nCompilation failed. Please check if Java is installed and the code is correct."
fi

# Clean up
rm TestFuzzyLogic.java
