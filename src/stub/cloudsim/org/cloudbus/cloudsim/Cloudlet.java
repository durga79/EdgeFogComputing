package org.cloudbus.cloudsim;

import java.util.List;

/**
 * Stub implementation of Cloudlet (task) for demonstration purposes
 */
public class Cloudlet {
    // Cloudlet status
    public static final int CREATED = 0;
    public static final int READY = 1;
    public static final int QUEUED = 2;
    public static final int INEXEC = 3;
    public static final int SUCCESS = 4;
    public static final int FAILED = 5;
    public static final int CANCELED = 6;
    public static final int PAUSED = 7;
    public static final int RESUMED = 8;
    public static final int FAILED_RESOURCE_UNAVAILABLE = 9;
    
    private int cloudletId;
    private int userId;
    private long cloudletLength;
    private int numberOfPes;
    private long cloudletFileSize;
    private long cloudletOutputSize;
    private int status;
    private int vmId;
    private double execStartTime;
    private double finishTime;
    
    // Additional properties for edge-fog computing
    private String wirelessProtocol;
    private double energyConsumption;
    private boolean secureOffloading;
    private String dataType;
    
    public Cloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, 
                   long cloudletOutputSize, UtilizationModel utilizationModelCpu,
                   UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw) {
        this.cloudletId = cloudletId;
        this.cloudletLength = cloudletLength;
        this.numberOfPes = pesNumber;
        this.cloudletFileSize = cloudletFileSize;
        this.cloudletOutputSize = cloudletOutputSize;
        this.status = CREATED;
        this.execStartTime = 0.0;
        this.finishTime = -1.0;
    }
    
    public int getCloudletId() {
        return cloudletId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public long getCloudletLength() {
        return cloudletLength;
    }
    
    public int getNumberOfPes() {
        return numberOfPes;
    }
    
    public long getCloudletFileSize() {
        return cloudletFileSize;
    }
    
    public long getCloudletOutputSize() {
        return cloudletOutputSize;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getVmId() {
        return vmId;
    }
    
    public void setVmId(int vmId) {
        this.vmId = vmId;
    }
    
    public double getExecStartTime() {
        return execStartTime;
    }
    
    public void setExecStartTime(double execStartTime) {
        this.execStartTime = execStartTime;
    }
    
    public double getFinishTime() {
        return finishTime;
    }
    
    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }
    
    public int getCloudletStatus() {
        return status;
    }
    
    public int getResourceId() {
        return vmId; // In CloudSim, resourceId is typically the datacenter ID, but we'll use vmId for simplicity
    }
    
    public double getActualCPUTime() {
        return finishTime - execStartTime;
    }
    
    // Edge-fog computing specific methods
    public String getWirelessProtocol() {
        return wirelessProtocol;
    }
    
    public void setWirelessProtocol(String wirelessProtocol) {
        this.wirelessProtocol = wirelessProtocol;
    }
    
    public double getEnergyConsumption() {
        return energyConsumption;
    }
    
    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
    
    public boolean isSecureOffloading() {
        return secureOffloading;
    }
    
    public void setSecureOffloading(boolean secureOffloading) {
        this.secureOffloading = secureOffloading;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
