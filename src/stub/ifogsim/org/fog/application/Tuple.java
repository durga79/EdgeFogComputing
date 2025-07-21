package org.fog.application;

/**
 * Stub implementation of Tuple for demonstration purposes
 */
public class Tuple {
    // Direction of tuples
    public static final int UP = 1;
    public static final int DOWN = 2;
    
    private String tupleType;
    private String srcModuleName;
    private String destModuleName;
    private int direction;
    private int userId;
    private String appId;
    private long dataSize;
    
    public Tuple(String tupleType, String srcModuleName, String destModuleName, int direction, int userId, String appId, long dataSize) {
        this.tupleType = tupleType;
        this.srcModuleName = srcModuleName;
        this.destModuleName = destModuleName;
        this.direction = direction;
        this.userId = userId;
        this.appId = appId;
        this.dataSize = dataSize;
    }
    
    public String getTupleType() {
        return tupleType;
    }
    
    public String getSrcModuleName() {
        return srcModuleName;
    }
    
    public String getDestModuleName() {
        return destModuleName;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public long getDataSize() {
        return dataSize;
    }
}
