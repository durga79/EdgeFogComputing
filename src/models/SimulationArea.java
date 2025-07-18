package models;

/**
 * Represents the simulation area with boundaries
 */
public class SimulationArea {
    private double width;
    private double height;
    
    /**
     * Create a simulation area with the given dimensions
     * @param width Width of the simulation area
     * @param height Height of the simulation area
     */
    public SimulationArea(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Get the width of the simulation area
     * @return Width
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Get the height of the simulation area
     * @return Height
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * Check if a location is within the simulation area
     * @param location The location to check
     * @return True if the location is within the area, false otherwise
     */
    public boolean contains(Location location) {
        return location.getX() >= 0 && location.getX() <= width &&
               location.getY() >= 0 && location.getY() <= height;
    }
    
    /**
     * Constrain a location to be within the simulation area
     * @param location The location to constrain
     * @return A new location that is within the simulation area
     */
    public Location constrain(Location location) {
        double x = Math.max(0, Math.min(width, location.getX()));
        double y = Math.max(0, Math.min(height, location.getY()));
        return new Location(x, y);
    }
}
