package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of VmSchedulerTimeShared for demonstration purposes
 */
public class VmSchedulerTimeShared extends VmScheduler {
    private Map<String, List<Double>> mipsMap;
    
    public VmSchedulerTimeShared(List<Pe> peList) {
        super(peList);
        mipsMap = new HashMap<>();
    }
    
    @Override
    public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        if (getAvailableMips() >= getTotalMipsFromList(mipsShare)) {
            mipsMap.put(vm.getUid(), new ArrayList<>(mipsShare));
            setAvailableMips(getAvailableMips() - getTotalMipsFromList(mipsShare));
            return true;
        }
        return false;
    }
    
    @Override
    public void deallocatePesForVm(Vm vm) {
        if (mipsMap.containsKey(vm.getUid())) {
            List<Double> mipsShare = mipsMap.remove(vm.getUid());
            setAvailableMips(getAvailableMips() + getTotalMipsFromList(mipsShare));
        }
    }
    
    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return mipsMap.getOrDefault(vm.getUid(), new ArrayList<>());
    }
    
    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        if (mipsMap.containsKey(vm.getUid())) {
            return getTotalMipsFromList(mipsMap.get(vm.getUid()));
        }
        return 0.0;
    }
    
    private double getTotalMipsFromList(List<Double> mipsList) {
        double totalMips = 0.0;
        for (Double mips : mipsList) {
            totalMips += mips;
        }
        return totalMips;
    }
}
