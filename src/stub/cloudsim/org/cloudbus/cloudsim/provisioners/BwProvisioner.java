package org.cloudbus.cloudsim.provisioners;

/**
 * Stub implementation of BwProvisioner for demonstration purposes
 */
public abstract class BwProvisioner {
    private long bw;
    private long availableBw;
    
    public BwProvisioner(long bw) {
        this.bw = bw;
        this.availableBw = bw;
    }
    
    public abstract boolean allocateBwForVm(String vmUid, long bw);
    
    public abstract long getAllocatedBwForVm(String vmUid);
    
    public abstract void deallocateBwForVm(String vmUid);
    
    public abstract void deallocateBwForAllVms();
    
    public long getBw() {
        return bw;
    }
    
    public long getAvailableBw() {
        return availableBw;
    }
    
    protected void setAvailableBw(long availableBw) {
        this.availableBw = availableBw;
    }
}
