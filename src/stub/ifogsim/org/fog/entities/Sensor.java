package org.fog.entities;

import org.fog.utils.Distribution;

/**
 * Stub implementation of Sensor for demonstration purposes
 */
public class Sensor {
    private String name;
    private String sensorType;
    private int gatewayDeviceId;
    private String appId;
    private Distribution transmitDistribution;
    
    public Sensor(String name, String sensorType, int gatewayDeviceId, String appId, Distribution transmitDistribution) {
        this.name = name;
        this.sensorType = sensorType;
        this.gatewayDeviceId = gatewayDeviceId;
        this.appId = appId;
        this.transmitDistribution = transmitDistribution;
        
        System.out.println("[iFogSim] Created Sensor: " + name + " of type " + sensorType);
    }
    
    public String getName() {
        return name;
    }
    
    public String getSensorType() {
        return sensorType;
    }
    
    public int getGatewayDeviceId() {
        return gatewayDeviceId;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public Distribution getTransmitDistribution() {
        return transmitDistribution;
    }
}
