package org.fog.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of Application for demonstration purposes
 */
public class Application {
    private String appId;
    private int userId;
    private List<AppModule> modules;
    private Map<String, AppEdge> edges;
    private Map<String, String> tupleMapping;
    
    private Application(String appId, int userId) {
        this.appId = appId;
        this.userId = userId;
        this.modules = new ArrayList<>();
        this.edges = new HashMap<>();
        this.tupleMapping = new HashMap<>();
        
        System.out.println("[iFogSim] Created Application: " + appId);
    }
    
    public static Application createApplication(String appId, int userId) {
        return new Application(appId, userId);
    }
    
    public void addAppModule(String moduleName, int ram, int mips, long size) {
        AppModule module = new AppModule(moduleName, appId, ram, mips, size);
        modules.add(module);
        System.out.println("[iFogSim] Added module " + moduleName + " to application " + appId);
    }
    
    public void addAppEdge(String source, String destination, long tupleCpuLength, 
                          long tupleNwLength, String tupleType, int edgeType, int direction) {
        AppEdge edge = new AppEdge(source, destination, tupleCpuLength, tupleNwLength, tupleType, direction, edgeType);
        edges.put(edge.getEdgeId(), edge);
        System.out.println("[iFogSim] Added edge from " + source + " to " + destination + " of type " + tupleType);
    }
    
    public void addTupleMapping(String sourceModule, String inputTupleType, String outputTupleType, double selectivity) {
        String key = sourceModule + ":" + inputTupleType + ":" + outputTupleType;
        tupleMapping.put(key, String.valueOf(selectivity));
        System.out.println("[iFogSim] Added tuple mapping for module " + sourceModule + ": " + 
                          inputTupleType + " -> " + outputTupleType + " with selectivity " + selectivity);
    }
    
    public String getAppId() {
        return appId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public List<AppModule> getModules() {
        return modules;
    }
    
    public Map<String, AppEdge> getEdges() {
        return edges;
    }
    
    public Map<String, String> getTupleMapping() {
        return tupleMapping;
    }
}
