package org.cloudbus.cloudsim;

import java.util.List;

/**
 * Stub implementation of CloudletSchedulerTimeShared for demonstration purposes
 */
public class CloudletSchedulerTimeShared extends CloudletScheduler {
    
    public CloudletSchedulerTimeShared() {
        super();
    }
    
    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        this.currentTime = currentTime;
        return 0.0; // Return the earliest completion time
    }
    
    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getCloudletId() == cloudletId) {
                cloudletList.remove(cloudlet);
                return cloudlet;
            }
        }
        return null;
    }
    
    @Override
    public boolean cloudletPause(int cloudletId) {
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getCloudletId() == cloudletId) {
                cloudlet.setStatus(Cloudlet.PAUSED);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean cloudletResume(int cloudletId) {
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getCloudletId() == cloudletId) {
                cloudlet.setStatus(Cloudlet.RESUMED);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }
    
    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        cloudlet.setStatus(Cloudlet.QUEUED);
        cloudletList.add(cloudlet);
        return 0.0;
    }
}
