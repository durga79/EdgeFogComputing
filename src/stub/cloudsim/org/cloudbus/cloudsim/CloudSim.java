package org.cloudbus.cloudsim;

import java.util.Calendar;
import java.util.List;

/**
 * Stub implementation of CloudSim for demonstration purposes
 */
public class CloudSim {
    private static boolean initialized = false;
    private static boolean running = false;
    
    public static void init(int numUsers, Calendar calendar, boolean traceEvents) {
        System.out.println("[CloudSim] Initializing CloudSim with " + numUsers + " users");
        initialized = true;
    }
    
    public static void startSimulation() {
        if (!initialized) {
            throw new IllegalStateException("CloudSim not initialized. Call init() first.");
        }
        System.out.println("[CloudSim] Starting simulation");
        running = true;
    }
    
    public static void stopSimulation() {
        if (!running) {
            throw new IllegalStateException("Simulation not running. Call startSimulation() first.");
        }
        System.out.println("[CloudSim] Stopping simulation");
        running = false;
    }
    
    public static double clock() {
        return System.currentTimeMillis() / 1000.0;
    }
}
