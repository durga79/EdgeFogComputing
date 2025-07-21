package org.fog.utils;

/**
 * Stub implementation of DeterministicDistribution for demonstration purposes
 */
public class DeterministicDistribution implements Distribution {
    private double value;
    
    public DeterministicDistribution(double value) {
        this.value = value;
    }
    
    @Override
    public double getNextValue() {
        return value;
    }
}
