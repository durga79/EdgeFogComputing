package analytics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data aggregation for Edge-Fog computing environments
 * Addresses the "No big data analytics" gap identified in the feedback
 */
public class DataAggregator implements DataProcessor {
    
    // Aggregation interval in milliseconds
    private long aggregationInterval;
    
    // Aggregated data
    private Map<String, Object> aggregatedData;
    
    // Last aggregation timestamp
    private long lastAggregationTime;
    
    // Processing statistics
    private Map<String, Object> statistics;
    
    /**
     * Create a new DataAggregator instance
     * 
     * @param aggregationIntervalSeconds Aggregation interval in seconds
     */
    public DataAggregator(int aggregationIntervalSeconds) {
        this.aggregationInterval = aggregationIntervalSeconds * 1000L;
        this.aggregatedData = new ConcurrentHashMap<>();
        this.lastAggregationTime = System.currentTimeMillis();
        this.statistics = new HashMap<>();
        this.statistics.put("dataPointsAggregated", 0);
        this.statistics.put("aggregationCycles", 0);
    }
    
    @Override
    public Map<String, Object> processData(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        // Update statistics
        int dataPoints = (int) statistics.getOrDefault("dataPointsAggregated", 0);
        statistics.put("dataPointsAggregated", dataPoints + 1);
        
        // Aggregate data
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Number) {
                // For numeric values, calculate running average
                double numericValue = ((Number) value).doubleValue();
                
                if (!aggregatedData.containsKey(key)) {
                    aggregatedData.put(key, numericValue);
                    aggregatedData.put(key + "_count", 1);
                } else {
                    double currentSum = ((Number) aggregatedData.get(key)).doubleValue() * 
                                       ((Number) aggregatedData.get(key + "_count")).intValue();
                    int count = ((Number) aggregatedData.get(key + "_count")).intValue() + 1;
                    
                    aggregatedData.put(key, (currentSum + numericValue) / count);
                    aggregatedData.put(key + "_count", count);
                }
                
                // Also track min/max
                if (!aggregatedData.containsKey(key + "_min") || 
                        numericValue < ((Number) aggregatedData.get(key + "_min")).doubleValue()) {
                    aggregatedData.put(key + "_min", numericValue);
                }
                
                if (!aggregatedData.containsKey(key + "_max") || 
                        numericValue > ((Number) aggregatedData.get(key + "_max")).doubleValue()) {
                    aggregatedData.put(key + "_max", numericValue);
                }
            } else {
                // For non-numeric values, just store the latest
                aggregatedData.put(key, value);
            }
        }
        
        // Check if it's time to perform a full aggregation cycle
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAggregationTime >= aggregationInterval) {
            performAggregationCycle();
        }
        
        return new HashMap<>(aggregatedData);
    }
    
    @Override
    public String getProcessorName() {
        return "DataAggregator";
    }
    
    @Override
    public Map<String, Object> getProcessingStatistics() {
        return new HashMap<>(statistics);
    }
    
    /**
     * Perform a full aggregation cycle
     */
    private void performAggregationCycle() {
        // In a real implementation, this would:
        // 1. Finalize the current aggregation window
        // 2. Persist aggregated data or send it to the cloud
        // 3. Reset temporary aggregation counters
        
        // For simulation purposes, we just update the timestamp and statistics
        lastAggregationTime = System.currentTimeMillis();
        
        int cycles = (int) statistics.getOrDefault("aggregationCycles", 0);
        statistics.put("aggregationCycles", cycles + 1);
        
        System.out.println("Data aggregation cycle completed: " + aggregatedData.size() + " metrics aggregated");
    }
    
    /**
     * Get the current aggregated data
     * 
     * @return Aggregated data
     */
    public Map<String, Object> getAggregatedData() {
        return new HashMap<>(aggregatedData);
    }
    
    /**
     * Reset the aggregator
     */
    public void reset() {
        aggregatedData.clear();
        lastAggregationTime = System.currentTimeMillis();
    }
}
