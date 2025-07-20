package energy;

/**
 * Battery model for IoT devices in Edge-Fog computing environments
 * Addresses the "No battery life modeling" gap identified in the feedback
 */
public class BatteryModel {
    
    // Battery capacity in milliampere-hours (mAh)
    private double capacityMAh;
    
    // Battery voltage in volts (V)
    private double voltageV;
    
    // Current charge level (0.0-1.0)
    private double chargeLevel;
    
    // Discharge rate in percentage per hour when idle
    private double idleDischargeRate;
    
    // Discharge efficiency factor (accounts for non-linear discharge)
    private double dischargeEfficiency;
    
    // Battery health factor (degrades over time, 0.0-1.0)
    private double healthFactor;
    
    // Energy consumed since last reset (in joules)
    private double energyConsumed;
    
    /**
     * Create a new BatteryModel with default parameters
     */
    public BatteryModel() {
        this(2000.0, 3.7, 1.0, 0.005, 0.9, 1.0);
    }
    
    /**
     * Create a new BatteryModel with custom capacity and default other parameters
     * 
     * @param capacityMAh Battery capacity in milliampere-hours
     */
    public BatteryModel(double capacityMAh) {
        this(capacityMAh, 3.7, 1.0, 0.005, 0.9, 1.0);
    }
    
    /**
     * Create a new BatteryModel with custom parameters
     * 
     * @param capacityMAh Battery capacity in milliampere-hours
     * @param voltageV Battery voltage in volts
     * @param initialChargeLevel Initial charge level (0.0-1.0)
     * @param idleDischargeRate Discharge rate in percentage per hour when idle
     * @param dischargeEfficiency Discharge efficiency factor
     * @param initialHealthFactor Initial battery health factor (0.0-1.0)
     */
    public BatteryModel(double capacityMAh, double voltageV, double initialChargeLevel,
                       double idleDischargeRate, double dischargeEfficiency, double initialHealthFactor) {
        this.capacityMAh = capacityMAh;
        this.voltageV = voltageV;
        this.chargeLevel = Math.max(0.0, Math.min(1.0, initialChargeLevel));
        this.idleDischargeRate = idleDischargeRate;
        this.dischargeEfficiency = dischargeEfficiency;
        this.healthFactor = Math.max(0.0, Math.min(1.0, initialHealthFactor));
        this.energyConsumed = 0.0;
    }
    
    /**
     * Get the total energy capacity of the battery in joules
     * 
     * @return Energy capacity in joules
     */
    public double getTotalEnergyCapacityJoules() {
        // E = capacity (mAh) * voltage (V) * 3.6 (conversion from mAh to joules)
        return capacityMAh * voltageV * 3.6 * healthFactor;
    }
    
    /**
     * Get the remaining energy in the battery in joules
     * 
     * @return Remaining energy in joules
     */
    public double getRemainingEnergyJoules() {
        return getTotalEnergyCapacityJoules() * chargeLevel;
    }
    
    /**
     * Consume energy from the battery
     * 
     * @param energyJoules Energy to consume in joules
     * @return True if sufficient energy was available, false otherwise
     */
    public boolean consumeEnergy(double energyJoules) {
        // Apply discharge efficiency factor (more energy is consumed than theoretically needed)
        double actualEnergyToConsume = energyJoules / dischargeEfficiency;
        
        // Calculate new charge level
        double totalCapacity = getTotalEnergyCapacityJoules();
        double newChargeLevel = chargeLevel - (actualEnergyToConsume / totalCapacity);
        
        if (newChargeLevel < 0.0) {
            // Not enough energy
            return false;
        }
        
        // Update charge level and energy consumed
        chargeLevel = newChargeLevel;
        energyConsumed += actualEnergyToConsume;
        
        return true;
    }
    
    /**
     * Simulate idle time and its effect on battery
     * 
     * @param idleTimeMs Idle time in milliseconds
     * @return True if battery is still charged after idle time, false if depleted
     */
    public boolean simulateIdle(long idleTimeMs) {
        // Calculate discharge during idle time
        double idleTimeHours = idleTimeMs / (1000.0 * 60.0 * 60.0);
        double dischargeAmount = idleTimeHours * idleDischargeRate;
        
        // Update charge level
        double newChargeLevel = chargeLevel - dischargeAmount;
        
        if (newChargeLevel < 0.0) {
            chargeLevel = 0.0;
            return false;
        }
        
        chargeLevel = newChargeLevel;
        
        // Calculate energy consumed during idle
        double energyJoulesConsumed = dischargeAmount * getTotalEnergyCapacityJoules();
        energyConsumed += energyJoulesConsumed;
        
        return true;
    }
    
    /**
     * Charge the battery
     * 
     * @param chargeAmount Amount to charge (0.0-1.0)
     */
    public void charge(double chargeAmount) {
        chargeLevel = Math.min(1.0, chargeLevel + chargeAmount);
    }
    
    /**
     * Discharge the battery by a specific amount of energy
     * 
     * @param energyJoules Energy to discharge in joules
     * @return True if discharge was successful, false if insufficient energy
     */
    public boolean discharge(double energyJoules) {
        return consumeEnergy(energyJoules);
    }
    
    /**
     * Fully charge the battery
     */
    public void fullyCharge() {
        chargeLevel = 1.0;
    }
    
    /**
     * Get the current charge level
     * 
     * @return Charge level (0.0-1.0)
     */
    public double getChargeLevel() {
        return chargeLevel;
    }
    
    /**
     * Get the battery health factor
     * 
     * @return Health factor (0.0-1.0)
     */
    public double getHealthFactor() {
        return healthFactor;
    }
    
    /**
     * Set the battery health factor (simulates battery aging)
     * 
     * @param healthFactor New health factor (0.0-1.0)
     */
    public void setHealthFactor(double healthFactor) {
        this.healthFactor = Math.max(0.0, Math.min(1.0, healthFactor));
    }
    
    /**
     * Get the energy consumed since last reset
     * 
     * @return Energy consumed in joules
     */
    public double getEnergyConsumed() {
        return energyConsumed;
    }
    
    /**
     * Reset the energy consumed counter
     */
    public void resetEnergyConsumed() {
        energyConsumed = 0.0;
    }
    
    /**
     * Get the estimated remaining battery life in hours based on a given power consumption
     * 
     * @param averagePowerConsumptionWatts Average power consumption in watts
     * @return Estimated remaining battery life in hours
     */
    public double getEstimatedRemainingLifeHours(double averagePowerConsumptionWatts) {
        if (averagePowerConsumptionWatts <= 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        
        // Calculate remaining energy and convert to watt-hours
        double remainingEnergyWh = getRemainingEnergyJoules() / 3600.0;
        
        // Calculate remaining time in hours
        return remainingEnergyWh / averagePowerConsumptionWatts;
    }
    
    @Override
    public String toString() {
        return String.format("Battery[%.1f mAh, %.1f V, %.1f%% charged, %.1f%% health]",
                            capacityMAh, voltageV, chargeLevel * 100.0, healthFactor * 100.0);
    }
}
