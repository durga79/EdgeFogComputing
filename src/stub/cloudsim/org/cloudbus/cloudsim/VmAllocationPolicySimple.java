package org.cloudbus.cloudsim;

import java.util.List;

/**
 * Stub implementation of VmAllocationPolicySimple for demonstration purposes
 */
public class VmAllocationPolicySimple extends VmAllocationPolicy {
    
    public VmAllocationPolicySimple(List<Host> hostList) {
        super(hostList);
    }
    
    @Override
    public boolean allocateHostForVm(Vm vm) {
        // Simple allocation - find first host with enough resources
        for (Host host : hostList) {
            if (host.vmCreate(vm)) {
                vmToHostMap.put(vm.getId(), host);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            vmToHostMap.put(vm.getId(), host);
            return true;
        }
        return false;
    }
}
