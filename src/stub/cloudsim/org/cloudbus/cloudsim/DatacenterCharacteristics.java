package org.cloudbus.cloudsim;

/**
 * Stub implementation of DatacenterCharacteristics for demonstration purposes
 */
public class DatacenterCharacteristics {
    public static final int ARCH_X86 = 0;
    public static final int OS_LINUX = 0;
    public static final int VMM_XEN = 0;
    private String architecture;
    private String os;
    private String vmm;
    private double costPerSecond;
    private double costPerMem;
    private double costPerStorage;
    private double costPerBw;
    private double mips;
    
    public DatacenterCharacteristics(String architecture, String os, String vmm, 
                                    double mips, int ram, long storage, long bw) {
        this.architecture = architecture;
        this.os = os;
        this.vmm = vmm;
        this.mips = mips;
        this.costPerSecond = 0.01;
        this.costPerMem = 0.02;
        this.costPerStorage = 0.001;
        this.costPerBw = 0.005;
    }
    
    public DatacenterCharacteristics(String architecture, String os, String vmm, 
                                    java.util.List<Host> hostList, double costPerSec, 
                                    double costPerMem, double costPerStorage, 
                                    double costPerBw, double mips) {
        this.architecture = architecture;
        this.os = os;
        this.vmm = vmm;
        this.mips = mips;
        this.costPerSecond = costPerSec;
        this.costPerMem = costPerMem;
        this.costPerStorage = costPerStorage;
        this.costPerBw = costPerBw;
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
    
    public double getCostPerSecond() {
        return costPerSecond;
    }
    
    public double getMips() {
        return mips;
    }
}
