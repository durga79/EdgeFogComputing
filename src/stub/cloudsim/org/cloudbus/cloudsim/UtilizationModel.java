package org.cloudbus.cloudsim;

/**
 * Stub implementation of UtilizationModel for demonstration purposes
 */
public interface UtilizationModel {
    /**
     * Gets the utilization percentage of a resource at a given simulation time.
     * 
     * @param time the time to get the resource utilization
     * @return the utilization percentage (between [0 and 1])
     */
    double getUtilization(double time);
}
