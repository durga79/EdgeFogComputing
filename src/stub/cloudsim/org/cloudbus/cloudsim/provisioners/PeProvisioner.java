package org.cloudbus.cloudsim.provisioners;

/**
 * Stub implementation of PeProvisioner for demonstration purposes
 */
public abstract class PeProvisioner {
    private double mips;
    private double availableMips;
    
    public PeProvisioner(double mips) {
        this.mips = mips;
        this.availableMips = mips;
    }
    
    public abstract boolean allocateMipsForVm(String vmUid, double mips);
    
    public abstract boolean allocateMipsForVm(String vmUid, double[] mips);
    
    public abstract void deallocateMipsForVm(String vmUid);
    
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
