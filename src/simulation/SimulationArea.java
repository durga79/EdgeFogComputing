package simulation;

/**
 * Represents the simulation area with boundaries for the simulation package
 */
public class SimulationArea {
    // Default dimensions for the simulation area
    public static final double WIDTH = 1000.0;
    public static final double HEIGHT = 1000.0;
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
}
