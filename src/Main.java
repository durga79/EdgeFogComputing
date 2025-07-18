import simulation.EdgeFogSimulation;

/**
 * Main entry point for the Edge-Fog Computing Task Offloading System
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Edge-Fog Computing Task Offloading System");
        System.out.println("Based on the research paper: \"A novel approach for IoT tasks offloading in edge-cloud environments\"");
        
        String configFile = "simulation/default_config.properties";
        if (args.length > 0) {
            configFile = args[0];
        }
        
        // Run the simulation
        EdgeFogSimulation simulation = new EdgeFogSimulation(configFile);
        simulation.initialize();
        simulation.runSimulation();
    }
}
