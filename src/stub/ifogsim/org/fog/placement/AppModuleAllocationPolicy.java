package org.fog.placement;

import org.fog.application.AppModule;
import org.fog.entities.FogDevice;
import java.util.List;

/**
 * Stub implementation of AppModuleAllocationPolicy for demonstration purposes
 */
public class AppModuleAllocationPolicy {
    private FogDevice fogDevice;
    private List<FogDevice> fogDevices;
    
    public AppModuleAllocationPolicy(FogDevice fogDevice) {
        this.fogDevice = fogDevice;
    }
    
    // Overloaded constructor to accept a list of fog devices
    public AppModuleAllocationPolicy(List<FogDevice> fogDevices) {
        this.fogDevices = fogDevices;
        if (fogDevices != null && !fogDevices.isEmpty()) {
            this.fogDevice = fogDevices.get(0);
        }
    }
    
    public boolean allocateHostForModule(AppModule module) {
        if (fogDevice != null) {
            System.out.println("[iFogSim] Allocating resources for module " + module.getName() + " on device " + fogDevice.getName());
        } else {
            System.out.println("[iFogSim] Allocating resources for module " + module.getName() + " on multiple devices");
        }
        return true;
    }
    
    public void deallocateHostForModule(AppModule module) {
        System.out.println("[iFogSim] Deallocating resources for module " + module.getName() + " on device " + fogDevice.getName());
    }
    
    public FogDevice getFogDevice() {
        return fogDevice;
    }
}
