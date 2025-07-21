package org.fog.scheduler;

/**
 * Stub implementation of StreamOperatorScheduler for demonstration purposes
 */
public class StreamOperatorScheduler {
    private double mips;
    private double availableMips;
    
    public StreamOperatorScheduler(double mips) {
        this.mips = mips;
        this.availableMips = mips;
        
        System.out.println("[iFogSim] Created StreamOperatorScheduler with " + mips + " MIPS");
    }
    
    public boolean allocateMipsForTask(double mipsRequired) {
        if (availableMips >= mipsRequired) {
            availableMips -= mipsRequired;
            return true;
        }
        return false;
    }
    
    public void deallocateMipsForTask(double mipsRequired) {
        availableMips += mipsRequired;
        if (availableMips > mips) {
            availableMips = mips;
        }
    }
    
    public double getAvailableMips() {
        return availableMips;
    }
    
    public double getTotalMips() {
        return mips;
    }
}
