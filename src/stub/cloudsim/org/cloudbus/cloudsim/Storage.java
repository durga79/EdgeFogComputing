package org.cloudbus.cloudsim;

/**
 * Stub implementation of Storage for demonstration purposes
 */
public class Storage {
    private String name;
    private long capacity;
    private long usedSpace;
    
    public Storage(String name, long capacity) {
        this.name = name;
        this.capacity = capacity;
        this.usedSpace = 0;
    }
    
    public String getName() {
        return name;
    }
    
    public long getCapacity() {
        return capacity;
    }
    
    public long getUsedSpace() {
        return usedSpace;
    }
    
    public long getAvailableSpace() {
        return capacity - usedSpace;
    }
    
    public boolean allocateSpace(long size) {
        if (getAvailableSpace() >= size) {
            usedSpace += size;
            return true;
        }
        return false;
    }
    
    public void deallocateSpace(long size) {
        if (size <= usedSpace) {
            usedSpace -= size;
        } else {
            usedSpace = 0;
        }
    }
}
