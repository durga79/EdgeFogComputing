package org.cloudbus.cloudsim;

/**
 * Stub implementation of UtilizationModelFull for demonstration purposes
 * This model assumes 100% utilization regardless of time
 */
public class UtilizationModelFull implements UtilizationModel {
    
    @Override
    public double getUtilization(double time) {
        return 1.0; // 100% utilization
    }
}
