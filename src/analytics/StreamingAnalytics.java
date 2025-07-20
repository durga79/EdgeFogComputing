package analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Real-time streaming analytics for Edge-Fog computing environments
 * Addresses the "No big data analytics" gap identified in the feedback
 */
public class StreamingAnalytics implements DataProcessor {
    
    // Window size for streaming analytics
    private int windowSize;
    
    // Data window
    private ConcurrentLinkedQueue<Map<String, Object>> dataWindow;
    
    // Processing statistics
    private Map<String, Object> statistics;
    
    /**
     * Create a new StreamingAnalytics instance
     * 
     * @param windowSize Size of the sliding window
     */
    public StreamingAnalytics(int windowSize) {
        this.windowSize = windowSize;
        this.dataWindow = new ConcurrentLinkedQueue<>();
        this.statistics = new HashMap<>();
        this.statistics.put("processedItems", 0);
        this.statistics.put("anomaliesDetected", 0);
    }
    
    @Override
    public Map<String, Object> processData(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        // Add data to window
        dataWindow.add(data);
        
        // Maintain window size
        while (dataWindow.size() > windowSize) {
            dataWindow.poll();
        }
        
        // Process window data
        Map<String, Object> result = analyzeWindow();
        
        // Update statistics
        int processed = (int) statistics.get("processedItems");
        statistics.put("processedItems", processed + 1);
        
        if (result.containsKey("anomaly") && (boolean) result.get("anomaly")) {
            int anomalies = (int) statistics.get("anomaliesDetected");
            statistics.put("anomaliesDetected", anomalies + 1);
        }
        
        return result;
    }
    
    @Override
    public String getProcessorName() {
        return "StreamingAnalytics";
    }
    
    @Override
    public Map<String, Object> getProcessingStatistics() {
        return new HashMap<>(statistics);
    }
    
    /**
     * Analyze the current data window
     * 
     * @return Analysis results
     */
    private Map<String, Object> analyzeWindow() {
        Map<String, Object> result = new HashMap<>();
        
        // Skip analysis if window is empty
        if (dataWindow.isEmpty()) {
            result.put("status", "empty");
            return result;
        }
        
        // Calculate basic statistics
        List<Double> numericValues = extractNumericValues();
        if (!numericValues.isEmpty()) {
            double mean = calculateMean(numericValues);
            double stdDev = calculateStdDev(numericValues, mean);
            
            result.put("mean", mean);
            result.put("stdDev", stdDev);
            result.put("count", numericValues.size());
            
            // Simple anomaly detection
            boolean anomaly = stdDev > mean * 0.5;
            result.put("anomaly", anomaly);
        }
        
        result.put("status", "processed");
        return result;
    }
    
    /**
     * Extract numeric values from the data window
     * 
     * @return List of numeric values
     */
    private List<Double> extractNumericValues() {
        List<Double> values = new ArrayList<>();
        
        for (Map<String, Object> data : dataWindow) {
            for (Object value : data.values()) {
                if (value instanceof Number) {
                    values.add(((Number) value).doubleValue());
                }
            }
        }
        
        return values;
    }
    
    /**
     * Calculate the mean of a list of values
     * 
     * @param values Values to calculate mean for
     * @return Mean value
     */
    private double calculateMean(List<Double> values) {
        double sum = 0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
    
    /**
     * Calculate the standard deviation of a list of values
     * 
     * @param values Values to calculate standard deviation for
     * @param mean Mean value
     * @return Standard deviation
     */
    private double calculateStdDev(List<Double> values, double mean) {
        double sumSquaredDiff = 0;
        for (Double value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        return Math.sqrt(sumSquaredDiff / values.size());
    }
}
