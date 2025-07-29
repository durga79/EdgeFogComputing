package visualization;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cloudsim_integration.CloudSimMain;
import ifogsim_integration.IFogSimMain;

/**
 * RunWithVisualization - A utility class to run simulations with direct visualization
 * This allows viewing chart PNGs directly in VSCode instead of opening them in a browser
 */
public class RunWithVisualization {

    public static void main(String[] args) {
        try {
            // Create timestamped simulation folder
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String simDirName = "simulation_results/" + timestamp;
            File simDir = new File(simDirName);
            if (!simDir.exists()) {
                simDir.mkdirs();
                System.out.println("Created simulation directory: " + simDir.getAbsolutePath());
            }

            // Run the simulation of your choice
            if (args.length > 0 && args[0].equalsIgnoreCase("ifogsim")) {
                System.out.println("Running iFogSim simulation with visualization...");
                runIFogSim(simDirName);
            } else {
                System.out.println("Running CloudSim simulation with visualization...");
                runCloudSim(simDirName);
            }
            
            // Optional: Generate task distribution chart (like the one in your screenshot)
            generateTaskDistributionChart(simDirName);
            
            System.out.println("\nVisualization complete!");
            System.out.println("You can now open the PNG files in " + simDirName + " directly in VSCode.");
            
        } catch (Exception e) {
            System.err.println("Error running simulation with visualization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runCloudSim(String outputDir) throws IOException {
        // Run CloudSim simulation
        CloudSimMain.main(new String[0]);
        
        // Generate charts from the results
        DirectVisualization.generateCharts("CloudSim", "results", outputDir);
    }
    
    private static void runIFogSim(String outputDir) throws IOException {
        // Run iFogSim simulation
        IFogSimMain.main(new String[0]);
        
        // Generate charts from the results
        DirectVisualization.generateCharts("IFogSim", "results", outputDir);
    }
    
    private static void generateTaskDistributionChart(String outputDir) {
        try {
            // Generate a task distribution chart similar to the one in the screenshot
            TaskDistributionChart.generateChart(outputDir + "/task_distribution.png");
            System.out.println("Generated task distribution chart: " + outputDir + "/task_distribution.png");
        } catch (Exception e) {
            System.err.println("Could not generate task distribution chart: " + e.getMessage());
        }
    }
}
