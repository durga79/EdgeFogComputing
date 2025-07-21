package org.fog.application;

/**
 * Stub implementation of AppEdge for demonstration purposes
 */
public class AppEdge {
    // Edge types
    public static final int SENSOR = 1;
    public static final int ACTUATOR = 2;
    public static final int MODULE = 3;
    
    private String source;
    private String destination;
    private String tupleType;
    private int direction;
    private int edgeType;
    private long tupleCpuLength;
    private long tupleNwLength;
    
    public AppEdge(String source, String destination, long tupleCpuLength, 
                 long tupleNwLength, String tupleType, int direction, int edgeType) {
        this.source = source;
        this.destination = destination;
        this.tupleCpuLength = tupleCpuLength;
        this.tupleNwLength = tupleNwLength;
        this.tupleType = tupleType;
        this.direction = direction;
        this.edgeType = edgeType;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public String getTupleType() {
        return tupleType;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public int getEdgeType() {
        return edgeType;
    }
    
    public long getTupleCpuLength() {
        return tupleCpuLength;
    }
    
    public long getTupleNwLength() {
        return tupleNwLength;
    }
    
    public String getEdgeId() {
        return source + "_" + destination + "_" + tupleType;
    }
}
