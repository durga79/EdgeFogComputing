package org.cloudbus.cloudsim.provisioners;

/**
 * Stub implementation of RamProvisioner for demonstration purposes
 */
public abstract class RamProvisioner {
    private int ram;
    private int availableRam;
    
    public RamProvisioner(int ram) {
        this.ram = ram;
        this.availableRam = ram;
    }
    
    public abstract boolean allocateRamForVm(String vmUid, int ram);
    
    public abstract int getAllocatedRamForVm(String vmUid);
    
    public abstract void deallocateRamForVm(String vmUid);
    
    public abstract void deallocateRamForAllVms();
    
    public int getRam() {
        return ram;
    }
    
    public int getAvailableRam() {
        return availableRam;
    }
    
    protected void setAvailableRam(int availableRam) {
        this.availableRam = availableRam;
    }
}
