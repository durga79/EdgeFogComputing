package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of DatacenterBroker for demonstration purposes
 */
public class DatacenterBroker {
    private String name;
    private int id;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Map<Integer, Integer> cloudletToVmMap;
    
    public DatacenterBroker(String name) throws Exception {
        this.name = name;
        this.id = name.hashCode();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.cloudletToVmMap = new HashMap<>();
        
        System.out.println("[CloudSim] Created DatacenterBroker: " + name);
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void submitVmList(List<Vm> list) {
        this.vmList.addAll(list);
        System.out.println("[CloudSim] DatacenterBroker " + name + " received " + list.size() + " VMs");
    }
    
    public void submitCloudletList(List<Cloudlet> list) {
        this.cloudletList.addAll(list);
        System.out.println("[CloudSim] DatacenterBroker " + name + " received " + list.size() + " Cloudlets");
    }
    
    public void bindCloudletToVm(int cloudletId, int vmId) {
        cloudletToVmMap.put(cloudletId, vmId);
    }
    
    public List<Cloudlet> getCloudletReceivedList() {
        return cloudletList;
    }
    
    public List<Vm> getVmList() {
        return vmList;
    }
}
