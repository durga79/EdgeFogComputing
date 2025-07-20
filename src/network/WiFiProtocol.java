package network;

/**
 * Implementation of WiFi wireless protocol
 * Based on typical WiFi specifications (802.11n)
 */
public class WiFiProtocol implements WirelessProtocol {
    
    private static final String NAME = "WiFi";
    private static final String TYPE = "WIFI";
    private static final double MAX_BANDWIDTH = 300000.0; // Kbps (300 Mbps)
    private static final double TYPICAL_LATENCY = 10.0; // ms
    private static final double MAX_RANGE = 100.0; // meters
    private static final double ENERGY_PER_BYTE = 0.03; // microjoules
    
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
        // WiFi bandwidth decreases with distance and interference
        double distanceFactor = Math.max(0.1, 1.0 - Math.pow(distance / MAX_RANGE, 1.5) * 0.9);
        double interferenceFactor = Math.max(0.1, 1.0 - interference * 0.9);
        return MAX_BANDWIDTH * distanceFactor * interferenceFactor;
    }
    
    @Override
    public double calculateActualLatency(double distance, double packetSize) {
        // WiFi latency increases with distance and packet size
        double distanceFactor = 1.0 + (distance / MAX_RANGE) * 1.0;
        double packetFactor = 1.0 + (packetSize / 1024.0) * 0.2; // Normalized to 1KB
        return TYPICAL_LATENCY * distanceFactor * packetFactor;
    }
}
