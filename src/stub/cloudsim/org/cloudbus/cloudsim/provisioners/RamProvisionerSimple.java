package org.cloudbus.cloudsim.provisioners;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of RamProvisionerSimple for demonstration purposes
 */
public class RamProvisionerSimple extends RamProvisioner {
    private Map<String, Integer> ramTable;
    
    public RamProvisionerSimple(int ram) {
        super(ram);
        ramTable = new HashMap<>();
    }
    
    @Override
    public boolean allocateRamForVm(String vmUid, int ram) {
        if (getAvailableRam() >= ram) {
            ramTable.put(vmUid, ram);
            setAvailableRam(getAvailableRam() - ram);
            return true;
        }
        return false;
    }
    
    @Override
    public int getAllocatedRamForVm(String vmUid) {
        return ramTable.getOrDefault(vmUid, 0);
    }
    
    @Override
    public void deallocateRamForVm(String vmUid) {
        if (ramTable.containsKey(vmUid)) {
            int allocatedRam = ramTable.remove(vmUid);
            setAvailableRam(getAvailableRam() + allocatedRam);
        }
    }
    
    @Override
    public void deallocateRamForAllVms() {
        ramTable.clear();
        setAvailableRam(getRam());
    }
}
