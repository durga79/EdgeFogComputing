package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation of Host for demonstration purposes
 */
public class Host {
    private int id;
    private double totalMips;
    private int ram;
    private long storage;
    private long bw;
    private List<Vm> vmList;
    
    public Host(int id, int ram, int bw, long storage, List<Pe> peList) {
        this.id = id;
        this.ram = ram;
        this.bw = bw;
        this.storage = storage;
        this.vmList = new ArrayList<>();
        
        // Calculate total MIPS
        this.totalMips = 0;
        for (Pe pe : peList) {
            this.totalMips += pe.getMips();
        }
    }
    
    public Host(int id, org.cloudbus.cloudsim.provisioners.RamProvisionerSimple ramProvisioner, 
               org.cloudbus.cloudsim.provisioners.BwProvisionerSimple bwProvisioner, 
               long storage, List<Pe> peList, 
               org.cloudbus.cloudsim.VmSchedulerTimeShared vmScheduler) {
        this.id = id;
        this.ram = ramProvisioner.getRam();
        this.bw = bwProvisioner.getBw();
        this.storage = storage;
        this.vmList = new ArrayList<>();
        
        // Calculate total MIPS
        this.totalMips = 0;
        for (Pe pe : peList) {
            this.totalMips += pe.getMips();
        }
    }
    
    public int getId() {
        return id;
    }
    
    public double getTotalMips() {
        return totalMips;
    }
    
    public int getRam() {
        return ram;
    }
    
    public long getStorage() {
        return storage;
    }
    
    public long getBw() {
        return bw;
    }
    
    public List<Vm> getVmList() {
        return vmList;
    }
    
    public boolean vmCreate(Vm vm) {
        vmList.add(vm);
        return true;
    }
    
    public void vmDestroy(Vm vm) {
        vmList.remove(vm);
    }
}
