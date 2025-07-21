package org.fog.placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fog.application.Application;
import org.fog.entities.FogDevice;

/**
 * Stub implementation of Controller for demonstration purposes
 */
public class Controller {
    private String name;
    private List<FogDevice> fogDevices;
    private List<Application> applications;
    private Map<String, ModuleMapping> appToModuleMapping;
    
    // Constructor for IFogSimManager usage
    public Controller(String name, List<FogDevice> fogDevices, List<org.fog.entities.Sensor> sensors, List<org.fog.entities.Actuator> actuators) {
        this.name = name;
        this.fogDevices = new ArrayList<>(fogDevices);
        this.applications = new ArrayList<>();
        this.appToModuleMapping = new HashMap<>();
        
        System.out.println("[iFogSim] Created Controller: " + name);
    }
    
    // Constructor for single sensor/actuator
    public Controller(String name, List<FogDevice> fogDevices, org.fog.entities.Sensor sensor, org.fog.entities.Actuator actuator) {
        this.name = name;
        this.fogDevices = new ArrayList<>(fogDevices);
        this.applications = new ArrayList<>();
        this.appToModuleMapping = new HashMap<>();
        
        System.out.println("[iFogSim] Created Controller: " + name);
    }
    
    public Controller(String name, List<FogDevice> fogDevices, List<Application> applications, Map<String, ModuleMapping> appToModuleMapping) {
        this.name = name;
        this.fogDevices = new ArrayList<>(fogDevices);
        this.applications = new ArrayList<>(applications);
        this.appToModuleMapping = new HashMap<>(appToModuleMapping);
        
        System.out.println("[iFogSim] Created Controller: " + name);
    }
    
    public void submitApplication(Application application, ModuleMapping moduleMapping) {
        applications.add(application);
        appToModuleMapping.put(application.getAppId(), moduleMapping);
        System.out.println("[iFogSim] Submitted application " + application.getAppId() + " to controller " + name);
    }
    
    public void startSimulation() {
        System.out.println("[iFogSim] Starting simulation with controller " + name);
        System.out.println("[iFogSim] Number of fog devices: " + fogDevices.size());
        System.out.println("[iFogSim] Number of applications: " + applications.size());
        
        // Process each application
        for (Application app : applications) {
            System.out.println("[iFogSim] Processing application: " + app.getAppId());
            ModuleMapping mapping = appToModuleMapping.get(app.getAppId());
            
            // Deploy modules according to mapping
            for (String moduleName : mapping.getModuleToDeviceMap().keySet()) {
                String deviceName = mapping.getDeviceForModule(moduleName);
                System.out.println("[iFogSim] Deploying module " + moduleName + " to device " + deviceName);
            }
        }
        
        System.out.println("[iFogSim] Simulation started successfully");
    }
    
    public void stopSimulation() {
        System.out.println("[iFogSim] Stopping simulation with controller " + name);
    }
    
    public String getName() {
        return name;
    }
    
    public List<FogDevice> getFogDevices() {
        return fogDevices;
    }
    
    public List<Application> getApplications() {
        return applications;
    }
}
