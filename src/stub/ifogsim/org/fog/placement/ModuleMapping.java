package org.fog.placement;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of ModuleMapping for demonstration purposes
 */
public class ModuleMapping {
    private Map<String, String> moduleToDeviceMap;
    
    public ModuleMapping() {
        this.moduleToDeviceMap = new HashMap<>();
    }
    
    /**
     * Static factory method to create a new ModuleMapping instance
     * @return A new ModuleMapping instance
     */
    public static ModuleMapping createModuleMapping() {
        return new ModuleMapping();
    }
    
    public void addModuleToDevice(String moduleName, String deviceName) {
        moduleToDeviceMap.put(moduleName, deviceName);
        System.out.println("[iFogSim] Mapped module " + moduleName + " to device " + deviceName);
    }
    
    public String getDeviceForModule(String moduleName) {
        return moduleToDeviceMap.get(moduleName);
    }
    
    public Map<String, String> getModuleToDeviceMap() {
        return moduleToDeviceMap;
    }
}
