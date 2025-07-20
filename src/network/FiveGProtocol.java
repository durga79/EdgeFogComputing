package network;

/**
 * Implementation of 5G wireless protocol
 * Based on typical 5G specifications
 */
public class FiveGProtocol implements WirelessProtocol {
    
    private static final String NAME = "5G";
    private static final String TYPE = "5G";
    private static final double MAX_BANDWIDTH = 1000000.0; // Kbps (1 Gbps)
    private static final double TYPICAL_LATENCY = 1.0; // ms
    private static final double MAX_RANGE = 1000.0; // meters
    private static final double ENERGY_PER_BYTE = 0.02; // microjoules
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getType() {
        return TYPE;
    }
    
    @Override
    public double getMaxBandwidth() {
        return MAX_BANDWIDTH;
    }
    
    @Override
    public double getTypicalLatency() {
        return TYPICAL_LATENCY;
    }
    
    @Override
    public double getMaxRange() {
        return MAX_RANGE;
    }
    
    @Override
    public double getEnergyPerByte() {
        return ENERGY_PER_BYTE;
    }
    
    @Override
    public double calculateActualBandwidth(double distance, double interference) {
        // 5G bandwidth decreases significantly with distance and interference
        double distanceFactor = Math.max(0.1, 1.0 - Math.pow(distance / MAX_RANGE, 2) * 0.9);
        double interferenceFactor = Math.max(0.2, 1.0 - interference * 0.8);
        return MAX_BANDWIDTH * distanceFactor * interferenceFactor;
    }
    
    @Override
    public double calculateActualLatency(double distance, double packetSize) {
        // 5G latency increases with distance and packet size but remains relatively low
        double distanceFactor = 1.0 + (distance / MAX_RANGE) * 0.5;
        double packetFactor = 1.0 + (packetSize / 1024.0) * 0.1; // Normalized to 1KB
        return TYPICAL_LATENCY * distanceFactor * packetFactor;
    }
}
