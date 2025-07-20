package bigdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of data aggregation mechanisms for edge computing
 * Addresses the "No data aggregation mechanisms" gap identified in the feedback
 */
public class DataAggregator implements DataProcessor {
    
    private static final String NAME = "Data Aggregator";
    private static final double COMPUTATIONAL_COMPLEXITY = 20.0; // MI per data point
    private static final double MEMORY_REQUIREMENT = 64.0; // MB
    
    // Aggregation time window in milliseconds
    private long timeWindow;
    
    // Buffer for storing data points within the time window
    private List<Map<String, Object>> dataBuffer;
    
    // Last aggregation timestamp
    private long lastAggregationTime;
    
    /**
     * Create a new DataAggregator with default time window
     */
    public DataAggregator() {
        this(60000); // Default time window of 60 seconds
    }
    
    /**
     * Create a new DataAggregator
     * 
     * @param timeWindowMs Time window for aggregation in milliseconds
     */
    public DataAggregator(long timeWindowMs) {
        this.timeWindow = timeWindowMs;
        this.dataBuffer = new ArrayList<>();
        this.lastAggregationTime = System.currentTimeMillis();
    }
    
    @Override
    public Map<String, Object> processBatch(List<Map<String, Object>> data) {
        // Add new data to buffer
        dataBuffer.addAll(data);
        
        // Check if it's time to aggregate
        long currentTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        if (currentTime - lastAggregationTime >= timeWindow) {
            // Time to aggregate data
            result = aggregateData();
            
            // Clear buffer and update timestamp
            dataBuffer.clear();
            lastAggregationTime = currentTime;
        } else {
            // Not time to aggregate yet
            result.put("status", "buffering");
            result.put("buffer_size", dataBuffer.size());
            result.put("next_aggregation_in_ms", timeWindow - (currentTime - lastAggregationTime));
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> processDataPoint(Map<String, Object> dataPoint) {
        List<Map<String, Object>> singlePointList = new ArrayList<>();
        singlePointList.add(dataPoint);
        return processBatch(singlePointList);
    }
    
    /**
     * Aggregate data in the buffer
     * 
     * @return Aggregated data
     */
    private Map<String, Object> aggregateData() {
        Map<String, Object> result = new HashMap<>();
        
        // Basic aggregation metrics
        result.put("count", dataBuffer.size());
        result.put("timestamp_start", lastAggregationTime);
        result.put("timestamp_end", System.currentTimeMillis());
        
        // Group data by source/device
        Map<String, List<Map<String, Object>>> dataBySource = new HashMap<>();
        
        for (Map<String, Object> dataPoint : dataBuffer) {
            String source = dataPoint.getOrDefault("source", "unknown").toString();
            if (!dataBySource.containsKey(source)) {
                dataBySource.put(source, new ArrayList<>());
            }
            dataBySource.get(source).add(dataPoint);
        }
        
        result.put("sources_count", dataBySource.size());
        
        // Calculate aggregates for each source
        Map<String, Map<String, Object>> aggregatesBySource = new HashMap<>();
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : dataBySource.entrySet()) {
            String source = entry.getKey();
            List<Map<String, Object>> sourceData = entry.getValue();
            
            Map<String, Object> sourceAggregates = new HashMap<>();
            sourceAggregates.put("count", sourceData.size());
            
            // Calculate min, max, sum, avg for numeric fields
            Map<String, Double> mins = new HashMap<>();
            Map<String, Double> maxs = new HashMap<>();
            Map<String, Double> sums = new HashMap<>();
            Map<String, Integer> counts = new HashMap<>();
            
            for (Map<String, Object> dataPoint : sourceData) {
                for (Map.Entry<String, Object> field : dataPoint.entrySet()) {
                    if (field.getValue() instanceof Number) {
                        String key = field.getKey();
                        double value = ((Number) field.getValue()).doubleValue();
                        
                        // Update min
                        if (!mins.containsKey(key) || value < mins.get(key)) {
                            mins.put(key, value);
                        }
                        
                        // Update max
                        if (!maxs.containsKey(key) || value > maxs.get(key)) {
                            maxs.put(key, value);
                        }
                        
                        // Update sum and count
                        sums.put(key, sums.getOrDefault(key, 0.0) + value);
                        counts.put(key, counts.getOrDefault(key, 0) + 1);
                    }
                }
            }
            
            // Calculate averages
            Map<String, Double> avgs = new HashMap<>();
            for (String key : sums.keySet()) {
                avgs.put(key, sums.get(key) / counts.get(key));
            }
            
            sourceAggregates.put("min", mins);
            sourceAggregates.put("max", maxs);
            sourceAggregates.put("sum", sums);
            sourceAggregates.put("avg", avgs);
            
            aggregatesBySource.put(source, sourceAggregates);
        }
        
        result.put("aggregates", aggregatesBySource);
        
        // Calculate data reduction ratio
        int originalDataSize = dataBuffer.size() * estimateDataPointSize();
        int aggregatedDataSize = estimateMapSize(result);
        double reductionRatio = 1.0 - ((double) aggregatedDataSize / originalDataSize);
        
        result.put("data_reduction_ratio", reductionRatio);
        
        return result;
    }
    
    /**
     * Estimate the size of a data point in bytes (rough approximation)
     * 
     * @return Estimated size in bytes
     */
    private int estimateDataPointSize() {
        // Rough estimate: 100 bytes per data point
        return 100;
    }
    
    /**
     * Estimate the size of a map in bytes (rough approximation)
     * 
     * @param map Map to estimate size for
     * @return Estimated size in bytes
     */
    private int estimateMapSize(Map<String, Object> map) {
        int size = 0;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Key size: 2 bytes per character
            size += entry.getKey().length() * 2;
            
            // Value size
            Object value = entry.getValue();
            if (value instanceof String) {
                size += ((String) value).length() * 2;
            } else if (value instanceof Number) {
                size += 8; // Assume 8 bytes for numbers
            } else if (value instanceof Map) {
                size += estimateMapSize((Map<String, Object>) value);
            } else if (value instanceof List) {
                for (Object item : (List) value) {
                    if (item instanceof Map) {
                        size += estimateMapSize((Map<String, Object>) item);
                    } else {
                        size += 8; // Rough estimate for other types
                    }
                }
            } else {
                size += 4; // Default size for other types
            }
        }
        
        return size;
    }
    
    /**
     * Get the aggregated data
     * 
     * @return Map of aggregated data
     */
    public Map<String, Object> getAggregatedData() {
        // Force aggregation if there's data in the buffer
        if (!dataBuffer.isEmpty()) {
            return aggregateData();
        }
        
        // Return the last aggregation result
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", lastAggregationTime);
        result.put("dataPoints", 0);
        result.put("aggregates", new HashMap<String, Object>());
        return result;
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
