package models;

/**
 * Represents a location in 2D space
 */
public class Location {
    private double x;
    private double y;
    
    /**
     * Create a location with the given coordinates
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
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
     * Calculate the Euclidean distance to another location
     * @param other The other location
     * @return The distance between this location and the other location
     */
    public double distanceTo(Location other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
