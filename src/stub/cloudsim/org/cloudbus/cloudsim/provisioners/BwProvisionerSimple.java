package org.cloudbus.cloudsim.provisioners;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of BwProvisionerSimple for demonstration purposes
 */
public class BwProvisionerSimple extends BwProvisioner {
    private Map<String, Long> bwTable;
    
    public BwProvisionerSimple(long bw) {
        super(bw);
        bwTable = new HashMap<>();
    }
    
    @Override
    public boolean allocateBwForVm(String vmUid, long bw) {
        if (getAvailableBw() >= bw) {
            bwTable.put(vmUid, bw);
            setAvailableBw(getAvailableBw() - bw);
            return true;
        }
        return false;
    }
    
    @Override
    public long getAllocatedBwForVm(String vmUid) {
        return bwTable.getOrDefault(vmUid, 0L);
    }
    
    @Override
    public void deallocateBwForVm(String vmUid) {
        if (bwTable.containsKey(vmUid)) {
            long allocatedBw = bwTable.remove(vmUid);
            setAvailableBw(getAvailableBw() + allocatedBw);
        }
    }
    
    @Override
    public void deallocateBwForAllVms() {
        bwTable.clear();
        setAvailableBw(getBw());
    }
}
