package cloudsim_integration;

/**
 * Main entry point for the CloudSim-based Edge-Fog Computing Task Offloading System
 * Based on the research paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
 * by Jaber Almutairi and Mohammad Aldossary
 */
public class CloudSimMain {
    public static void main(String[] args) {
        System.out.println("Edge-Fog Computing Task Offloading System using CloudSim");
        System.out.println("Based on the research paper: \"A novel approach for IoT tasks offloading in edge-cloud environments\"");
        System.out.println("by Jaber Almutairi and Mohammad Aldossary");
        
        // Configuration parameters
        int numEdgeNodes = 5;
        int numIoTDevices = 50;
        double simulationTime = 30.0;
        boolean advancedFeaturesEnabled = true;
        
        // Allow command line overrides
        if (args.length >= 1) {
            try {
                numEdgeNodes = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of edge nodes. Using default: " + numEdgeNodes);
            }
        }
        
        if (args.length >= 2) {
            try {
                numIoTDevices = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of IoT devices. Using default: " + numIoTDevices);
            }
        }
        
        if (args.length >= 3) {
            try {
                simulationTime = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid simulation time. Using default: " + simulationTime);
            }
        }
        
        if (args.length >= 4) {
            advancedFeaturesEnabled = Boolean.parseBoolean(args[3]);
        }
        
        System.out.println("\nSimulation Configuration:");
        System.out.println("- Number of Edge Nodes: " + numEdgeNodes);
        System.out.println("- Number of IoT Devices: " + numIoTDevices);
        System.out.println("- Simulation Time: " + simulationTime);
        System.out.println("- Advanced Features: " + (advancedFeaturesEnabled ? "Enabled" : "Disabled"));
        System.out.println();
        
        // Create CloudSim manager
        CloudSimManager manager = new CloudSimManager(numEdgeNodes, numIoTDevices, simulationTime, advancedFeaturesEnabled);
        
        // Initialize the simulation
        System.out.println("Initializing CloudSim simulation...");
        manager.initialize();
        
        // Run the simulation
        System.out.println("Running CloudSim simulation...");
        manager.runSimulation();
        
        System.out.println("\nSimulation completed.");
    }
}
