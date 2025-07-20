package visualization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizes simulation results using console output
 */
public class ResultsVisualizer {
    
    private List<Double> timeSteps;
    private List<Double> localEdgeServiceTimes;
    private List<Double> otherEdgeServiceTimes;
    private List<Double> cloudServiceTimes;
    private List<Double> edgeUtilizations;
    private List<Double> cloudUtilizations;
    
    public ResultsVisualizer() {
        timeSteps = new ArrayList<>();
        localEdgeServiceTimes = new ArrayList<>();
        otherEdgeServiceTimes = new ArrayList<>();
        cloudServiceTimes = new ArrayList<>();
        edgeUtilizations = new ArrayList<>();
        cloudUtilizations = new ArrayList<>();
    }
    
    /**
     * Load data from CSV file
     * @param filePath Path to CSV file
     */
    public void loadDataFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            String line = br.readLine();
            
            // Read data
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 6) {
                    timeSteps.add(Double.parseDouble(values[0]));
                    localEdgeServiceTimes.add(Double.parseDouble(values[1]));
                    otherEdgeServiceTimes.add(Double.parseDouble(values[2]));
                    cloudServiceTimes.add(Double.parseDouble(values[3]));
                    edgeUtilizations.add(Double.parseDouble(values[4]));
                    cloudUtilizations.add(Double.parseDouble(values[5]));
                }
            }
            
            System.out.println("Loaded " + timeSteps.size() + " data points from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
    
    /**
     * Display service time comparison in console
     */
    private void displayServiceTimeComparison() {
        System.out.println("\n===== SERVICE TIME COMPARISON =====");
        System.out.println("Time\tLocal Edge\tOther Edge\tCloud");
        
        // Calculate averages for summary
        double localEdgeAvg = 0;
        double otherEdgeAvg = 0;
        double cloudAvg = 0;
        
        // Display data points (limited to avoid console spam)
        int step = Math.max(1, timeSteps.size() / 10); // Show at most 10 data points
        for (int i = 0; i < timeSteps.size(); i += step) {
            System.out.printf("%.1f\t%.2f\t\t%.2f\t\t%.2f%n", 
                timeSteps.get(i), 
                localEdgeServiceTimes.get(i),
                otherEdgeServiceTimes.get(i),
                cloudServiceTimes.get(i));
                
            localEdgeAvg += localEdgeServiceTimes.get(i);
            otherEdgeAvg += otherEdgeServiceTimes.get(i);
            cloudAvg += cloudServiceTimes.get(i);
        }
        
        // Display averages
        if (!timeSteps.isEmpty()) {
            localEdgeAvg /= timeSteps.size();
            otherEdgeAvg /= timeSteps.size();
            cloudAvg /= timeSteps.size();
            
            System.out.println("\nAVERAGES:");
            System.out.printf("Local Edge: %.2f ms%n", localEdgeAvg);
            System.out.printf("Other Edge: %.2f ms%n", otherEdgeAvg);
            System.out.printf("Cloud: %.2f ms%n", cloudAvg);
        }
    }
    
    /**
     * Display utilization comparison in console
     */
    private void displayUtilizationComparison() {
        System.out.println("\n===== RESOURCE UTILIZATION COMPARISON =====");
        System.out.println("Time\tEdge Nodes\tCloud");
        
        // Calculate averages for summary
        double edgeAvg = 0;
        double cloudAvg = 0;
        
        // Display data points (limited to avoid console spam)
        int step = Math.max(1, timeSteps.size() / 10); // Show at most 10 data points
        for (int i = 0; i < timeSteps.size(); i += step) {
            System.out.printf("%.1f\t%.2f%%\t\t%.2f%%%n", 
                timeSteps.get(i), 
                edgeUtilizations.get(i),
                cloudUtilizations.get(i));
                
            edgeAvg += edgeUtilizations.get(i);
            cloudAvg += cloudUtilizations.get(i);
        }
        
        // Display averages
        if (!timeSteps.isEmpty()) {
            edgeAvg /= timeSteps.size();
            cloudAvg /= timeSteps.size();
            
            System.out.println("\nAVERAGE UTILIZATION:");
            System.out.printf("Edge Nodes: %.2f%%%n", edgeAvg);
            System.out.printf("Cloud: %.2f%%%n", cloudAvg);
        }
    }
    
    /**
     * Display all results in console
     */
    public void displayResults() {
        System.out.println("\n========================================");
        System.out.println("EDGE-FOG COMPUTING SIMULATION RESULTS");
        System.out.println("========================================");
        
        if (timeSteps.isEmpty()) {
            System.out.println("No data available to display.");
            return;
        }
        
        displayServiceTimeComparison();
        displayUtilizationComparison();
        
        System.out.println("\n========================================");
    }
    
    /**
     * Main method to test visualization
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        String resultsFile = "results/simulation_results.csv";
        if (args.length > 0) {
            resultsFile = args[0];
        }
        
        ResultsVisualizer visualizer = new ResultsVisualizer();
        visualizer.loadDataFromCSV(resultsFile);
        visualizer.displayResults();
    }
}
