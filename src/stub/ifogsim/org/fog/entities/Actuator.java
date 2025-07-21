package org.fog.entities;

/**
 * Stub implementation of Actuator for demonstration purposes
 */
public class Actuator {
    private String name;
    private String actuatorType;
    private String appId;
    private int gatewayDeviceId;
    
    public Actuator(String name, String appId, int gatewayDeviceId) {
        this.name = name;
        this.actuatorType = "GENERIC";
        this.appId = appId;
        this.gatewayDeviceId = gatewayDeviceId;
        
        System.out.println("[iFogSim] Created Actuator: " + name);
    }
    
    public String getName() {
        return name;
    }
    
    public String getActuatorType() {
        return actuatorType;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public int getGatewayDeviceId() {
        return gatewayDeviceId;
    }
}
