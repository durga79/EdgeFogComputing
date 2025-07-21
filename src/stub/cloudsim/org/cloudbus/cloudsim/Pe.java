package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * Stub implementation of Processing Element (Pe) for demonstration purposes
 */
public class Pe {
    private int id;
    private double mips;
    private int status;
    private PeProvisioner peProvisioner;
    
    // Pe status
    public static final int FREE = 0;
    public static final int BUSY = 1;
    public static final int FAILED = 2;
    
    public Pe(int id, double mips) {
        this.id = id;
        this.mips = mips;
        this.status = FREE;
        this.peProvisioner = new PeProvisionerSimple(mips);
    }
    
    public Pe(int id, PeProvisioner peProvisioner) {
        this.id = id;
        this.mips = peProvisioner.getMips();
        this.status = FREE;
        this.peProvisioner = peProvisioner;
    }
    
    public int getId() {
        return id;
    }
    
    public double getMips() {
        return mips;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}
