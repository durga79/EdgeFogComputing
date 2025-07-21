package org.cloudbus.cloudsim.provisioners;

/**
 * Stub implementation of PeProvisionerSimple for demonstration purposes
 */
public class PeProvisionerSimple extends PeProvisioner {
    
    public PeProvisionerSimple(double mips) {
        super(mips);
    }
    
    @Override
    public boolean allocateMipsForVm(String vmUid, double mips) {
        if (getAvailableMips() >= mips) {
            setAvailableMips(getAvailableMips() - mips);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean allocateMipsForVm(String vmUid, double[] mips) {
        double totalMips = 0;
        for (double mip : mips) {
            totalMips += mip;
        }
        
        if (getAvailableMips() >= totalMips) {
            setAvailableMips(getAvailableMips() - totalMips);
            return true;
        }
        return false;
    }
    
    @Override
    public void deallocateMipsForVm(String vmUid) {
        // In a real implementation, this would track per-VM allocations
        // For the stub, we'll just assume all MIPS are available
        setAvailableMips(getMips());
    }
}
