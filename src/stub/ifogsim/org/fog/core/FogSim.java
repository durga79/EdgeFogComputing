package org.fog.core;

import java.util.Calendar;

/**
 * Stub implementation of FogSim for demonstration purposes
 */
public class FogSim {
    private static boolean initialized = false;
    private static double clock = 0.0;
    
    public static void init(int numUsers, Calendar calendar, boolean traceFlag) {
        initialized = true;
        clock = 0.0;
        System.out.println("[iFogSim] Initialized with " + numUsers + " users");
    }
    
    public static boolean startSimulation() {
        if (!initialized) {
            System.out.println("[iFogSim] Error: FogSim not initialized");
            return false;
        }
        
        System.out.println("[iFogSim] Starting simulation");
        clock = 0.0;
        return true;
    }
    
    public static void stopSimulation() {
        System.out.println("[iFogSim] Stopping simulation at time " + clock);
        initialized = false;
    }
    
    public static double clock() {
        return clock;
    }
    
    public static void setClock(double time) {
        clock = time;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
