import simulation.EdgeFogSimulation;
import cloudsim_integration.CloudSimManager;
import ifogsim_integration.IFogSimManager;

/**
 * Launcher class that allows selecting between different simulation implementations:
 * 1. Custom simulation framework
 * 2. CloudSim-based implementation
 * 3. iFogSim-based implementation
 * 
 * Based on the research paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
 * by Jaber Almutairi and Mohammad Aldossary
 */
public class SimulationLauncher {
    public static void main(String[] args) {
        System.out.println("Edge-Fog Computing Task Offloading System");
        System.out.println("Based on the research paper: \"A novel approach for IoT tasks offloading in edge-cloud environments\"");
        System.out.println("by Jaber Almutairi and Mohammad Aldossary");
        
        // Default simulation type
        String simulationType = "custom";
        
        // Configuration parameters
        int numEdgeNodes = 5;
        int numIoTDevices = 50;
        double simulationTime = 30.0;
        boolean advancedFeaturesEnabled = true;
        String configFile = "simulation/default_config.properties";
        
        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--type") && i + 1 < args.length) {
                simulationType = args[i + 1].toLowerCase();
                i++;
            } else if (args[i].equals("--edge-nodes") && i + 1 < args.length) {
                try {
                    numEdgeNodes = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number of edge nodes. Using default: " + numEdgeNodes);
                }
            } else if (args[i].equals("--iot-devices") && i + 1 < args.length) {
                try {
                    numIoTDevices = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number of IoT devices. Using default: " + numIoTDevices);
                }
            } else if (args[i].equals("--time") && i + 1 < args.length) {
                try {
                    simulationTime = Double.parseDouble(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid simulation time. Using default: " + simulationTime);
                }
            } else if (args[i].equals("--config") && i + 1 < args.length) {
                configFile = args[i + 1];
                i++;
            } else if (args[i].equals("--basic")) {
                advancedFeaturesEnabled = false;
            }
        }
        
        System.out.println("\nSimulation Configuration:");
        System.out.println("- Simulation Type: " + simulationType);
        System.out.println("- Number of Edge Nodes: " + numEdgeNodes);
        System.out.println("- Number of IoT Devices: " + numIoTDevices);
        System.out.println("- Simulation Time: " + simulationTime);
        System.out.println("- Advanced Features: " + (advancedFeaturesEnabled ? "Enabled" : "Disabled"));
        if (simulationType.equals("custom")) {
            System.out.println("- Config File: " + configFile);
        }
        System.out.println();
        
        // Run the selected simulation type
        switch (simulationType) {
            case "cloudsim":
                runCloudSimSimulation(numEdgeNodes, numIoTDevices, simulationTime, advancedFeaturesEnabled);
                break;
            case "ifogsim":
                runIFogSimSimulation(numEdgeNodes, numIoTDevices, simulationTime, advancedFeaturesEnabled);
                break;
            case "custom":
            default:
                runCustomSimulation(configFile);
                break;
        }
    }
    
    /**
     * Run the custom simulation implementation
     */
    private static void runCustomSimulation(String configFile) {
        System.out.println("Running custom simulation implementation...");
        
        // Create and run the simulation
        EdgeFogSimulation simulation = new EdgeFogSimulation(configFile);
        simulation.initialize();
        simulation.runSimulation();
        
        System.out.println("\nCustom simulation completed.");
    }
    
    /**
     * Run the CloudSim-based simulation
     */
    private static void runCloudSimSimulation(int numEdgeNodes, int numIoTDevices, double simulationTime, boolean advancedFeaturesEnabled) {
        System.out.println("Running CloudSim-based simulation...");
        
        // Create CloudSim manager
        CloudSimManager manager = new CloudSimManager(numEdgeNodes, numIoTDevices, simulationTime, advancedFeaturesEnabled);
        
        // Initialize the simulation
        System.out.println("Initializing CloudSim simulation...");
        manager.initialize();
        
        // Run the simulation
        System.out.println("Running CloudSim simulation...");
        manager.runSimulation();
        
        System.out.println("\nCloudSim simulation completed.");
    }
    
    /**
     * Run the iFogSim-based simulation
     */
    private static void runIFogSimSimulation(int numEdgeNodes, int numIoTDevices, double simulationTime, boolean advancedFeaturesEnabled) {
        System.out.println("Running iFogSim-based simulation...");
        
        // Create iFogSim manager
        IFogSimManager manager = new IFogSimManager(numEdgeNodes, numIoTDevices, simulationTime, advancedFeaturesEnabled);
        
        // Initialize the simulation
        System.out.println("Initializing iFogSim simulation...");
        manager.initialize();
        
        // Run the simulation
        System.out.println("Running iFogSim simulation...");
        manager.runSimulation();
        
        System.out.println("\niFogSim simulation completed.");
    }
}
