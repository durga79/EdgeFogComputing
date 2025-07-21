package org.fog.entities;

/**
 * Stub implementation of FogDeviceCharacteristics for demonstration purposes
 */
public class FogDeviceCharacteristics {
    private String architecture;
    private String os;
    private String vmm;
    private double mips;
    private int ram;
    private long storage;
    private long bw;
    
    public FogDeviceCharacteristics(String architecture, String os, String vmm, 
                                  double mips, int ram, long bw, long downBw, long storage) {
        this.architecture = architecture;
        this.os = os;
        this.vmm = vmm;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.bw = bw;
    }
    
    public String getArchitecture() {
        return architecture;
    }
    
    public String getOs() {
        return os;
    }
    
    public String getVmm() {
        return vmm;
    }
    
    public double getMips() {
        return mips;
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
}
