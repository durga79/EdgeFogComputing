package bigdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of streaming analytics for real-time data processing
 * Addresses the "No streaming analytics" gap identified in the feedback
 */
public class StreamingAnalytics implements DataProcessor {
    
    private static final String NAME = "Streaming Analytics";
    private static final double COMPUTATIONAL_COMPLEXITY = 50.0; // MI per data point
    private static final double MEMORY_REQUIREMENT = 128.0; // MB
    
    // Window size for sliding window analytics
    private int windowSize;
    
    // Buffer for storing recent data points
    private List<Map<String, Object>> dataBuffer;
    
    /**
     * Create a new StreamingAnalytics processor with default window size
     */
    public StreamingAnalytics() {
        this(100); // Default window size of 100
    }
    
    /**
     * Create a new StreamingAnalytics processor
     * 
     * @param windowSize Size of the sliding window for analytics
     */
    public StreamingAnalytics(int windowSize) {
        this.windowSize = windowSize;
        this.dataBuffer = new ArrayList<>(windowSize);
    }
    
    @Override
    public Map<String, Object> processBatch(List<Map<String, Object>> data) {
        Map<String, Object> result = new HashMap<>();
        
        // Add new data to buffer
        dataBuffer.addAll(data);
        
        // Keep only the most recent data points
        if (dataBuffer.size() > windowSize) {
            dataBuffer = dataBuffer.subList(dataBuffer.size() - windowSize, dataBuffer.size());
        }
        
        // Calculate statistics over the window
        result.put("count", dataBuffer.size());
        result.put("timestamp", System.currentTimeMillis());
        
        // Calculate averages for numeric fields
        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        
        for (Map<String, Object> dataPoint : dataBuffer) {
            for (Map.Entry<String, Object> entry : dataPoint.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    double value = ((Number) entry.getValue()).doubleValue();
                    sums.put(entry.getKey(), sums.getOrDefault(entry.getKey(), 0.0) + value);
                    counts.put(entry.getKey(), counts.getOrDefault(entry.getKey(), 0) + 1);
                }
            }
        }
        
        Map<String, Double> averages = new HashMap<>();
        for (String key : sums.keySet()) {
            averages.put(key + "_avg", sums.get(key) / counts.get(key));
        }
        
        result.put("metrics", averages);
        
        // Detect anomalies (simple threshold-based)
        List<Map<String, Object>> anomalies = new ArrayList<>();
        for (Map<String, Object> dataPoint : dataBuffer) {
            for (String key : averages.keySet()) {
                String originalKey = key.replace("_avg", "");
                if (dataPoint.containsKey(originalKey) && dataPoint.get(originalKey) instanceof Number) {
                    double value = ((Number) dataPoint.get(originalKey)).doubleValue();
                    double avg = averages.get(key);
                    
                    // If value is more than 2x the average, flag as anomaly
                    if (value > avg * 2) {
                        Map<String, Object> anomaly = new HashMap<>();
                        anomaly.put("field", originalKey);
                        anomaly.put("value", value);
                        anomaly.put("average", avg);
                        anomaly.put("timestamp", dataPoint.getOrDefault("timestamp", "unknown"));
                        anomalies.add(anomaly);
                    }
                }
            }
        }
        
        result.put("anomalies", anomalies);
        
        return result;
    }
    
    @Override
    public Map<String, Object> processDataPoint(Map<String, Object> dataPoint) {
        List<Map<String, Object>> singlePointList = new ArrayList<>();
        singlePointList.add(dataPoint);
        return processBatch(singlePointList);
    }
    
    /**
     * Get the current statistics from the streaming analytics
     * 
     * @return Map of statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("windowSize", windowSize);
        stats.put("currentDataPoints", dataBuffer.size());
        stats.put("lastUpdateTime", System.currentTimeMillis());
        
        // Add some basic statistics if we have data
        if (!dataBuffer.isEmpty()) {
            // Calculate averages for common numeric fields
            Map<String, Double> sums = new HashMap<>();
            Map<String, Integer> counts = new HashMap<>();
            
            for (Map<String, Object> dataPoint : dataBuffer) {
                for (Map.Entry<String, Object> entry : dataPoint.entrySet()) {
                    if (entry.getValue() instanceof Number) {
                        double value = ((Number) entry.getValue()).doubleValue();
                        sums.put(entry.getKey(), sums.getOrDefault(entry.getKey(), 0.0) + value);
                        counts.put(entry.getKey(), counts.getOrDefault(entry.getKey(), 0) + 1);
                    }
                }
            }
            
            Map<String, Double> averages = new HashMap<>();
            for (String key : sums.keySet()) {
                averages.put(key + "_avg", sums.get(key) / counts.get(key));
            }
            
            stats.put("metrics", averages);
        }
        
        return stats;
    }
    
    /**
     * Process a single data point
     * 
     * @param data Data to process
     */
    public void processData(Map<String, Object> data) {
        if (data == null) return;
        
        List<Map<String, Object>> singleDataPoint = new ArrayList<>();
        singleDataPoint.add(data);
        processBatch(singleDataPoint);
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public double getComputationalComplexity() {
        return COMPUTATIONAL_COMPLEXITY;
    }
    
    @Override
    public double getMemoryRequirement() {
        return MEMORY_REQUIREMENT;
    }
}
