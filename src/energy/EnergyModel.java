package energy;

/**
 * Interface for energy consumption models in Edge-Fog computing environments
 * Addresses the "No energy efficiency modeling" gap identified in the feedback
 */
public interface EnergyModel {
    
    /**
     * Calculate energy consumption for computation
     * 
     * @param cpuUtilization CPU utilization (0.0-1.0)
     * @param executionTimeMs Execution time in milliseconds
     * @param mips Million instructions per second of the device
     * @return Energy consumption in joules
     */
    double calculateComputationEnergy(double cpuUtilization, long executionTimeMs, double mips);
    
    /**
     * Calculate energy consumption for data transmission
     * 
     * @param dataSize Data size in bytes
     * @param transmissionRate Transmission rate in bytes per second
     * @param transmissionPower Transmission power in milliwatts
     * @return Energy consumption in joules
     */
    double calculateTransmissionEnergy(long dataSize, double transmissionRate, double transmissionPower);
    
    /**
     * Calculate energy consumption for data reception
     * 
     * @param dataSize Data size in bytes
     * @param receptionRate Reception rate in bytes per second
     * @param receptionPower Reception power in milliwatts
     * @return Energy consumption in joules
     */
    double calculateReceptionEnergy(long dataSize, double receptionRate, double receptionPower);
    
    /**
     * Calculate idle energy consumption
     * 
     * @param idleTimeMs Idle time in milliseconds
     * @param idlePower Idle power consumption in milliwatts
     * @return Energy consumption in joules
     */
    double calculateIdleEnergy(long idleTimeMs, double idlePower);
    
    /**
     * Get the name of the energy model
     * 
     * @return Energy model name
     */
    String getName();
}
