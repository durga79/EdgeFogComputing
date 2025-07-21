package org.fog.application;

/**
 * Stub implementation of AppModule for demonstration purposes
 */
public class AppModule {
    private String name;
    private String appId;
    private int ram;
    private int mips;
    private long size;
    
    public AppModule(String name, String appId, int ram, int mips, long size) {
        this.name = name;
        this.appId = appId;
        this.ram = ram;
        this.mips = mips;
        this.size = size;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public int getRam() {
        return ram;
    }
    
    public int getMips() {
        return mips;
    }
    
    public long getSize() {
        return size;
    }
}
