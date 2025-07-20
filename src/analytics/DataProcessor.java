package analytics;

import java.util.Map;

/**
 * Interface for data processing components in Edge-Fog computing environments
 * Addresses the "No big data analytics" gap identified in the feedback
 */
public interface DataProcessor {
    
    /**
     * Process data from a task or IoT device
     * 
     * @param data Data to process
     * @return Processing result or null if processing failed
     */
    Map<String, Object> processData(Map<String, Object> data);
    
    /**
     * Get the name of the processor
     * 
     * @return Processor name
     */
    String getProcessorName();
    
    /**
     * Get statistics about processed data
     * 
     * @return Statistics map
     */
    Map<String, Object> getProcessingStatistics();
}
