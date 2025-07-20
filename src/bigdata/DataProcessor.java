package bigdata;

import java.util.List;
import java.util.Map;

/**
 * Interface for data processing algorithms in the Edge-Fog computing environment
 * Addresses the Big Data Analytics gap identified in the feedback
 */
public interface DataProcessor {
    /**
     * Process a batch of data
     * 
     * @param data List of data points to process
     * @return Processed result
     */
    Map<String, Object> processBatch(List<Map<String, Object>> data);
    
    /**
     * Process a single data point
     * 
     * @param dataPoint Single data point to process
     * @return Processed result
     */
    Map<String, Object> processDataPoint(Map<String, Object> dataPoint);
    
    /**
     * Get the name of the processor
     * 
     * @return Processor name
     */
    String getName();
    
    /**
     * Get the computational complexity of the processor
     * 
     * @return Computational complexity in Million Instructions (MI) per data point
     */
    double getComputationalComplexity();
    
    /**
     * Get the memory requirement of the processor
     * 
     * @return Memory requirement in MB
     */
    double getMemoryRequirement();
}
