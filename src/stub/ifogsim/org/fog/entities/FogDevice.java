package org.fog.entities;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;

/**
 * Stub implementation of FogDevice for demonstration purposes
 */
public class FogDevice {
    private int id;
    private String name;
    private int level;
    private int parentId;
    private double uplinkLatency;
    private double ratePerMips;
    private Host host;
    private List<Sensor> sensors;
    private List<Actuator> actuators;
    
    public FogDevice(String name, FogDeviceCharacteristics characteristics, 
                    Object vmAllocationPolicy, List<Sensor> sensors, 
                    List<Actuator> actuators, double schedulingInterval, 
                    double uplinkLatency, double ratePerMips, 
                    double costPerMem, double costPerStorage) {
        this.id = name.hashCode();
        this.name = name;
        this.level = 0;
        this.parentId = -1;
        this.uplinkLatency = uplinkLatency;
        this.ratePerMips = ratePerMips;
        this.sensors = new ArrayList<>(sensors);
        this.actuators = new ArrayList<>(actuators);
        
        // Create a host for this fog device
        List<org.cloudbus.cloudsim.Pe> peList = new ArrayList<>();
        peList.add(new org.cloudbus.cloudsim.Pe(0, characteristics.getMips()));
        
        this.host = new Host(0, characteristics.getRam(), 
                           (int)characteristics.getBw(), 
                           characteristics.getStorage(), peList);
        
        System.out.println("[iFogSim] Created FogDevice: " + name);
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getParentId() {
        return parentId;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    public double getUplinkLatency() {
        return uplinkLatency;
    }
    
    public double getRatePerMips() {
        return ratePerMips;
    }
    
    public void setRatePerMips(double ratePerMips) {
        this.ratePerMips = ratePerMips;
    }
    
    public Host getHost() {
        return host;
    }
    
    public List<Sensor> getSensors() {
        return sensors;
    }
    
    public List<Actuator> getActuators() {
        return actuators;
    }
}
