package org.cloudbus.cloudsim;

import java.util.List;

/**
 * Stub implementation of VmScheduler for demonstration purposes
 */
public abstract class VmScheduler {
    private List<Pe> peList;
    private double mips;
    private double availableMips;
    
    public VmScheduler(List<Pe> peList) {
        this.peList = peList;
        
        // Calculate total MIPS
        this.mips = 0.0;
        for (Pe pe : peList) {
            this.mips += pe.getMips();
        }
        this.availableMips = this.mips;
    }
    
    public abstract boolean allocatePesForVm(Vm vm, List<Double> mipsShare);
    
    public abstract void deallocatePesForVm(Vm vm);
    
    public abstract List<Double> getAllocatedMipsForVm(Vm vm);
    
    public abstract double getTotalAllocatedMipsForVm(Vm vm);
    
    public List<Pe> getPeList() {
        return peList;
    }
    
    public double getMips() {
        return mips;
    }
    
    public double getAvailableMips() {
        return availableMips;
    }
    
    protected void setAvailableMips(double availableMips) {
        this.availableMips = availableMips;
    }
}
