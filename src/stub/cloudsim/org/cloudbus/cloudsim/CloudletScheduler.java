package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation of CloudletScheduler for demonstration purposes
 */
public abstract class CloudletScheduler {
    protected List<Cloudlet> cloudletList;
    protected double currentTime;
    
    public CloudletScheduler() {
        this.cloudletList = new ArrayList<>();
        this.currentTime = 0.0;
    }
    
    public abstract double updateVmProcessing(double currentTime, List<Double> mipsShare);
    
    public abstract Cloudlet cloudletCancel(int cloudletId);
    
    public abstract boolean cloudletPause(int cloudletId);
    
    public abstract boolean cloudletResume(int cloudletId);
    
    public abstract double cloudletSubmit(Cloudlet cloudlet);
    
    public abstract double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime);
    
    public List<Cloudlet> getCloudletList() {
        return cloudletList;
    }
    
    public double getCurrentTime() {
        return currentTime;
    }
}
