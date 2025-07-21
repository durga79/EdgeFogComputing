package models;

/**
 * Represents a location in 3D space
 */
public class Location {
    private double x;
    private double y;
    private double z;
    
    /**
     * Create a location with the given coordinates
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate (optional, defaults to 0)
     */
    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Create a location with the given 2D coordinates (z=0)
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Location(double x, double y) {
        this(x, y, 0.0);
    }
    
    /**
     * Get the X coordinate
     * @return X coordinate
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get the Y coordinate
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }
    
    /**
     * Get the Z coordinate
     * @return Z coordinate
     */
    public double getZ() {
        return z;
    }
    
    /**
     * Calculate the Euclidean distance to another location
     * @param other The other location
     * @return The distance between this location and the other location
     */
    public double distanceTo(Location other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
