package network;

/**
 * Implementation of LoRaWAN wireless protocol
 * Based on typical LoRaWAN specifications
 */
public class LoRaWANProtocol implements WirelessProtocol {
    
    private static final String NAME = "LoRaWAN";
    private static final String TYPE = "LORA";
    private static final double MAX_BANDWIDTH = 50.0; // Kbps
    private static final double TYPICAL_LATENCY = 1000.0; // ms
    private static final double MAX_RANGE = 10000.0; // meters
    private static final double ENERGY_PER_BYTE = 0.1; // microjoules
    
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
        // LoRaWAN bandwidth decreases with distance but is less affected by interference
        double distanceFactor = Math.max(0.1, 1.0 - (distance / MAX_RANGE) * 0.9);
        double interferenceFactor = Math.max(0.5, 1.0 - interference * 0.5);
        return MAX_BANDWIDTH * distanceFactor * interferenceFactor;
    }
    
    @Override
    public double calculateActualLatency(double distance, double packetSize) {
        // LoRaWAN latency increases with distance and packet size
        double distanceFactor = 1.0 + (distance / MAX_RANGE) * 2.0;
        double packetFactor = 1.0 + (packetSize / 1024.0) * 0.5; // Normalized to 1KB
        return TYPICAL_LATENCY * distanceFactor * packetFactor;
    }
}
