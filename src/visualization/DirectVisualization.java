package visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DirectVisualization generates charts directly to a specified folder without HTML dashboard
 * This allows viewing charts directly in the IDE rather than in a browser
 */
public class DirectVisualization {

    /**
     * Overloaded method for backward compatibility
     * @param simulationType The type of simulation ("CloudSim" or "IFogSim")
     * @param inputDir Directory containing the CSV files
     * @return The path to the output directory where charts were saved
     */
    public static String generateCharts(String simulationType, String inputDir) {
        return generateCharts(simulationType, inputDir, null);
    }
    
    /**
     * Generate all charts for a simulation run into a specific output directory
     * @param simulationType The type of simulation ("CloudSim" or "IFogSim")
     * @param inputDir Directory containing the CSV files
     * @param outputDir Directory where charts should be saved (if null, creates a timestamped directory)
     * @return The path to the output directory where charts were saved
     */
    public static String generateCharts(String simulationType, String inputDir, String outputDir) {
        try {
            // Use the provided outputDir if available, otherwise create a timestamped directory
            String resultDirName;
            if (outputDir == null || outputDir.isEmpty()) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                resultDirName = "simulation_results/" + simulationType + "_" + timestamp;
            } else {
                resultDirName = outputDir;
            }
            
            File resultsDir = new File(resultDirName);
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
                System.out.println("Created results directory: " + resultsDir.getAbsolutePath());
            }

            // Input files (based on simulation type)
            String simResultsFile = inputDir + "/" + 
                (simulationType.equals("CloudSim") ? "simulation_results.csv" : "ifogsim_results.csv");
            String advancedResultsFile = inputDir + "/" +
                (simulationType.equals("CloudSim") ? "advanced_results.csv" : "ifogsim_advanced_results.csv");
            String protocolUsageFile = inputDir + "/" +
                (simulationType.equals("CloudSim") ? "protocol_usage.csv" : "ifogsim_protocol_usage.csv");

            // Generate charts
            generateServiceTimeChart(simResultsFile, resultDirName + "/service_time_chart.png");
            generateResourceUtilizationChart(simResultsFile, resultDirName + "/resource_utilization_chart.png");
            generateEnergyConsumptionChart(advancedResultsFile, resultDirName + "/energy_consumption_chart.png");
            generateProtocolUsageChart(protocolUsageFile, resultDirName + "/protocol_usage_chart.png");
            
            // Copy CSV files to the results directory for reference (if needed)
            /*
            copyFile(new File(simResultsFile), new File(resultDirName + "/" + 
                (simulationType.equals("CloudSim") ? "simulation_results.csv" : "ifogsim_results.csv")));
            copyFile(new File(advancedResultsFile), new File(resultDirName + "/" + 
                (simulationType.equals("CloudSim") ? "advanced_results.csv" : "ifogsim_advanced_results.csv")));
            copyFile(new File(protocolUsageFile), new File(resultDirName + "/" + 
                (simulationType.equals("CloudSim") ? "protocol_usage.csv" : "ifogsim_protocol_usage.csv")));
            */
            
            System.out.println("Charts generated successfully in: " + resultDirName);
            System.out.println("You can open these PNG files directly in your editor.");
            
            return resultDirName;
            
        } catch (Exception e) {
            System.err.println("Error generating visualizations: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a bar chart for service time comparison
     */
    private static void generateServiceTimeChart(String csvFile, String outputFile) throws IOException {
        // Create dataset from CSV
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String taskId = "Task " + values[0];
                    double serviceTime = Double.parseDouble(values[3]); // Using execution time column
                    dataset.addValue(serviceTime, "Service Time (ms)", taskId);
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Service Time per Task",
            "Task ID",
            "Service Time (milliseconds)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 128, 255)); // Blue bars
        
        // Save chart to PNG file
        File chartFile = new File(outputFile);
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated: " + chartFile.getAbsolutePath());
    }

    /**
     * Generate a line chart for resource utilization
     */
    private static void generateResourceUtilizationChart(String csvFile, String outputFile) throws IOException {
        // Create dataset from CSV
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data (sample every few lines to avoid overcrowding)
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (lineCount % 3 != 0) continue; // Sample every 3rd line
                
                String[] values = line.split(",");
                if (values.length >= 5) {
                    String timePoint = "T" + lineCount;
                    double utilization = Double.parseDouble(values[4]); // CPU utilization
                    dataset.addValue(utilization * 100, "CPU Utilization", timePoint); // Convert to percentage
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
            "Resource Utilization",
            "Time Point",
            "Utilization (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Save chart to PNG file
        File chartFile = new File(outputFile);
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated: " + chartFile.getAbsolutePath());
    }

    /**
     * Generate a line chart for energy consumption
     */
    private static void generateEnergyConsumptionChart(String csvFile, String outputFile) throws IOException {
        // Create dataset from CSV
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data (sample every few lines to avoid overcrowding)
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (lineCount % 2 != 0) continue; // Sample every 2nd line
                
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String deviceId = "Dev" + values[0];
                    double energy = Double.parseDouble(values[2]); // Energy consumption
                    dataset.addValue(energy, "Energy (J)", deviceId);
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Energy Consumption by Device",
            "Device ID",
            "Energy (Joules)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(76, 153, 0)); // Green bars
        
        // Save chart to PNG file
        File chartFile = new File(outputFile);
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated: " + chartFile.getAbsolutePath());
    }

    /**
     * Generate a pie chart for protocol usage
     */
    private static void generateProtocolUsageChart(String csvFile, String outputFile) throws IOException {
        // Create dataset from CSV
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String protocol = values[0];
                    double percentage = Double.parseDouble(values[2].replace("%", "")); // Usage percentage
                    dataset.addValue(percentage, "Usage (%)", protocol);
                }
            }
        }
        
        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Wireless Protocol Usage Distribution",
            "Protocol",
            "Usage (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 102, 0)); // Orange bars
        
        // Save chart to PNG file
        File chartFile = new File(outputFile);
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        System.out.println("Generated: " + chartFile.getAbsolutePath());
    }
}
