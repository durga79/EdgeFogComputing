package network;

/**
 * Interface for wireless protocols in the Edge-Fog computing environment
 * Extends the simulation to support different wireless technologies as mentioned in the paper
 */
public interface WirelessProtocol {
    /**
     * Get the protocol name
     * @return Protocol name (e.g., "LoRaWAN", "NB-IoT", "5G", "WiFi")
     */
    String getName();
    
    /**
     * Get the protocol type identifier
     * @return Protocol type identifier (e.g., "LORA", "NBIOT", "5G", "WIFI")
     */
    String getType();
    
    /**
     * Get the maximum bandwidth of the protocol in Kbps
     * @return Maximum bandwidth
     */
    double getMaxBandwidth();
    
    /**
     * Get the typical latency of the protocol in milliseconds
     * @return Typical latency
     */
    double getTypicalLatency();
    
    /**
     * Get the maximum range of the protocol in meters
     * @return Maximum range
     */
    double getMaxRange();
    
    /**
     * Get the energy consumption per byte transmitted in microjoules
     * @return Energy consumption
     */
    double getEnergyPerByte();
    
    /**
     * Calculate the actual bandwidth based on distance and conditions
     * @param distance Distance between device and access point in meters
     * @param interference Level of interference (0-1)
     * @return Actual bandwidth in Kbps
     */
    double calculateActualBandwidth(double distance, double interference);
    
    /**
     * Calculate the actual latency based on distance and conditions
     * @param distance Distance between device and access point in meters
     * @param packetSize Size of the packet in bytes
     * @return Actual latency in milliseconds
     */
    double calculateActualLatency(double distance, double packetSize);
}
