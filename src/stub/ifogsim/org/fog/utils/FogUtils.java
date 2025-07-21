package org.fog.utils;

/**
 * Stub implementation of FogUtils for demonstration purposes
 */
public class FogUtils {
    private static int ENTITY_ID_COUNTER = 0;
    
    public static int generateEntityId() {
        return ++ENTITY_ID_COUNTER;
    }
    
    public static double getLatency(int srcDeviceId, int destDeviceId) {
        // Simplified latency calculation for demonstration
        return 10.0; // Default 10ms latency
    }
    
    public static double getPowerConsumption(double cpuLoad) {
        // Simplified power consumption model
        return 100 + (cpuLoad * 200); // Base 100W + load-dependent component
    }
}
