package energy;

/**
 * Linear energy consumption model for Edge-Fog computing environments
 * Implements a simple linear model for energy consumption calculations
 */
public class LinearEnergyModel implements EnergyModel {
    
    private static final String NAME = "Linear Energy Model";
    
    // Coefficient for CPU energy consumption (joules per MIPS per millisecond)
    private double cpuEnergyCoefficient;
    
    // Base power consumption when idle (milliwatts)
    private double basePower;
    
    /**
     * Create a new LinearEnergyModel with default parameters
     */
    public LinearEnergyModel() {
        this(0.0001, 100.0); // Default values
    }
    
    /**
     * Create a new LinearEnergyModel with custom energy coefficient
     * 
     * @param energyConsumptionRate CPU energy coefficient (joules per MIPS per millisecond)
     */
    public LinearEnergyModel(double energyConsumptionRate) {
        this(energyConsumptionRate, 100.0); // Use default base power
    }
    
    /**
     * Create a new LinearEnergyModel with custom parameters
     * 
     * @param cpuEnergyCoefficient CPU energy coefficient (joules per MIPS per millisecond)
     * @param basePower Base power consumption when idle (milliwatts)
     */
    public LinearEnergyModel(double cpuEnergyCoefficient, double basePower) {
        this.cpuEnergyCoefficient = cpuEnergyCoefficient;
        this.basePower = basePower;
    }
    
    @Override
    public double calculateComputationEnergy(double cpuUtilization, long executionTimeMs, double mips) {
        // E = (base_power + cpu_utilization * mips * coefficient) * time_in_seconds
        double executionTimeSeconds = executionTimeMs / 1000.0;
        double dynamicPower = cpuUtilization * mips * cpuEnergyCoefficient * 1000; // Convert to watts
        double totalPower = (basePower / 1000.0) + dynamicPower; // Convert base power to watts
        
        return totalPower * executionTimeSeconds; // Energy in joules
    }
    
    @Override
    public double calculateTransmissionEnergy(long dataSize, double transmissionRate, double transmissionPower) {
        // E = power * time
        // time = dataSize / transmissionRate
        double transmissionTimeSeconds = dataSize / transmissionRate;
        double powerWatts = transmissionPower / 1000.0; // Convert milliwatts to watts
        
        return powerWatts * transmissionTimeSeconds; // Energy in joules
    }
    
    @Override
    public double calculateReceptionEnergy(long dataSize, double receptionRate, double receptionPower) {
        // E = power * time
        // time = dataSize / receptionRate
        double receptionTimeSeconds = dataSize / receptionRate;
        double powerWatts = receptionPower / 1000.0; // Convert milliwatts to watts
        
        return powerWatts * receptionTimeSeconds; // Energy in joules
    }
    
    @Override
    public double calculateIdleEnergy(long idleTimeMs, double idlePower) {
        // E = power * time
        double idleTimeSeconds = idleTimeMs / 1000.0;
        double powerWatts = idlePower / 1000.0; // Convert milliwatts to watts
        
        return powerWatts * idleTimeSeconds; // Energy in joules
    }
    
    @Override
    public String getName() {
        return NAME;
    }
}
