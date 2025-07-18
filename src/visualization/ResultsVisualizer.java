package visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizes simulation results using JFreeChart
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
            System.err.println("Error loading data from CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create service time comparison chart
     * @return JFreeChart object
     */
    public JFreeChart createServiceTimeChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        XYSeries localEdgeSeries = new XYSeries("Local Edge");
        XYSeries otherEdgeSeries = new XYSeries("Other Edge");
        XYSeries cloudSeries = new XYSeries("Cloud");
        
        for (int i = 0; i < timeSteps.size(); i++) {
            localEdgeSeries.add(timeSteps.get(i), localEdgeServiceTimes.get(i));
            otherEdgeSeries.add(timeSteps.get(i), otherEdgeServiceTimes.get(i));
            cloudSeries.add(timeSteps.get(i), cloudServiceTimes.get(i));
        }
        
        dataset.addSeries(localEdgeSeries);
        dataset.addSeries(otherEdgeSeries);
        dataset.addSeries(cloudSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Service Time Comparison",
            "Time Step",
            "Service Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // Set line colors
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.RED);
        
        // Show shapes at data points
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesVisible(2, true);
        
        plot.setRenderer(renderer);
        
        return chart;
    }
    
    /**
     * Create resource utilization chart
     * @return JFreeChart object
     */
    public JFreeChart createUtilizationChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        XYSeries edgeSeries = new XYSeries("Edge Nodes");
        XYSeries cloudSeries = new XYSeries("Cloud");
        
        for (int i = 0; i < timeSteps.size(); i++) {
            edgeSeries.add(timeSteps.get(i), edgeUtilizations.get(i));
            cloudSeries.add(timeSteps.get(i), cloudUtilizations.get(i));
        }
        
        dataset.addSeries(edgeSeries);
        dataset.addSeries(cloudSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Resource Utilization",
            "Time Step",
            "CPU Utilization (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // Set line colors
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        
        // Show shapes at data points
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        
        plot.setRenderer(renderer);
        
        return chart;
    }
    
    /**
     * Display charts in a frame
     */
    public void displayCharts() {
        JFrame frame = new JFrame("Edge-Fog Computing Simulation Results");
        frame.setLayout(new GridLayout(2, 1));
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create service time chart
        JFreeChart serviceTimeChart = createServiceTimeChart();
        ChartPanel serviceTimePanel = new ChartPanel(serviceTimeChart);
        frame.add(serviceTimePanel);
        
        // Create utilization chart
        JFreeChart utilizationChart = createUtilizationChart();
        ChartPanel utilizationPanel = new ChartPanel(utilizationChart);
        frame.add(utilizationPanel);
        
        frame.setVisible(true);
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
        visualizer.displayCharts();
    }
}
