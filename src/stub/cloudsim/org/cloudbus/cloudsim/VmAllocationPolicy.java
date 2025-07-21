package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of VmAllocationPolicy for demonstration purposes
 */
public abstract class VmAllocationPolicy {
    protected List<Host> hostList;
    protected Map<Integer, Host> vmToHostMap;
    
    public VmAllocationPolicy(List<Host> hostList) {
        this.hostList = hostList;
        this.vmToHostMap = new HashMap<>();
    }
    
    public abstract boolean allocateHostForVm(Vm vm);
    
    public abstract boolean allocateHostForVm(Vm vm, Host host);
    
    public void deallocateHostForVm(Vm vm) {
        Host host = vmToHostMap.remove(vm.getId());
        if (host != null) {
            host.vmDestroy(vm);
        }
    }
    
    public Host getHost(Vm vm) {
        return vmToHostMap.get(vm.getId());
    }
    
    public Host getHost(int vmId) {
        return vmToHostMap.get(vmId);
    }
    
    public List<Host> getHostList() {
        return hostList;
    }
}
