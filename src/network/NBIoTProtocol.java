package network;

/**
 * Implementation of NB-IoT (Narrowband Internet of Things) wireless protocol
 * Based on typical NB-IoT specifications
 */
public class NBIoTProtocol implements WirelessProtocol {
    
    private static final String NAME = "NB-IoT";
    private static final String TYPE = "NBIOT";
    private static final double MAX_BANDWIDTH = 250.0; // Kbps
    private static final double TYPICAL_LATENCY = 1500.0; // ms
    private static final double MAX_RANGE = 15000.0; // meters
    private static final double ENERGY_PER_BYTE = 0.05; // microjoules
    
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
        // NB-IoT bandwidth decreases with distance and interference
        double distanceFactor = Math.max(0.2, 1.0 - (distance / MAX_RANGE) * 0.8);
        double interferenceFactor = Math.max(0.3, 1.0 - interference * 0.7);
        return MAX_BANDWIDTH * distanceFactor * interferenceFactor;
    }
    
    @Override
    public double calculateActualLatency(double distance, double packetSize) {
        // NB-IoT latency increases with distance and packet size
        double distanceFactor = 1.0 + (distance / MAX_RANGE) * 1.5;
        double packetFactor = 1.0 + (packetSize / 1024.0) * 0.3; // Normalized to 1KB
        return TYPICAL_LATENCY * distanceFactor * packetFactor;
    }
}
