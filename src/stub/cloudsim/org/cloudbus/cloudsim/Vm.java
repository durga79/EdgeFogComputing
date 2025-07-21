package org.cloudbus.cloudsim;

/**
 * Stub implementation of Virtual Machine (Vm) for demonstration purposes
 */
public class Vm {
    private int id;
    private int userId;
    private double mips;
    private int numberOfPes;
    private int ram;
    private long bw;
    private long size;
    private String vmm;
    private CloudletScheduler cloudletScheduler;
    
    public Vm(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm, CloudletScheduler cloudletScheduler) {
        this.id = id;
        this.userId = userId;
        this.mips = mips;
        this.numberOfPes = numberOfPes;
        this.ram = ram;
        this.bw = bw;
        this.size = size;
        this.vmm = vmm;
        this.cloudletScheduler = cloudletScheduler;
    }
    
    public int getId() {
        return id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public double getMips() {
        return mips;
    }
    
    public int getNumberOfPes() {
        return numberOfPes;
    }
    
    public int getRam() {
        return ram;
    }
    
    public long getBw() {
        return bw;
    }
    
    public long getSize() {
        return size;
    }
    
    public String getVmm() {
        return vmm;
    }
    
    public CloudletScheduler getCloudletScheduler() {
        return cloudletScheduler;
    }
    
    public String getUid() {
        return userId + "-" + id;
    }
}
