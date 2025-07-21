package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation of Datacenter for demonstration purposes
 */
public class Datacenter {
    private int id;
    private String name;
    private List<Host> hostList;
    private DatacenterCharacteristics characteristics;
    
    public Datacenter(String name, DatacenterCharacteristics characteristics, 
                     VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, 
                     double schedulingInterval) throws Exception {
        this.id = name.hashCode();
        this.name = name;
        this.characteristics = characteristics;
        this.hostList = new ArrayList<>();
        
        System.out.println("[CloudSim] Created datacenter: " + name);
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Host> getHostList() {
        return hostList;
    }
    
    public void addHost(Host host) {
        this.hostList.add(host);
    }
    
    public DatacenterCharacteristics getCharacteristics() {
        return characteristics;
    }
}
