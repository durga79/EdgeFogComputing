package network;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating and managing wireless protocol instances
 */
public class WirelessProtocolFactory {
    
    private static final Map<String, WirelessProtocol> protocols = new HashMap<>();
    
    static {
        // Initialize available protocols
        protocols.put("LORAWAN", new LoRaWANProtocol());
        protocols.put("NBIOT", new NBIoTProtocol());
        protocols.put("5G", new FiveGProtocol());
        protocols.put("WIFI", new WiFiProtocol());
    }
    
    /**
     * Get a wireless protocol instance by name
     * 
     * @param protocolName Name of the protocol (case insensitive)
     * @return WirelessProtocol instance or null if not found
     */
    public static WirelessProtocol getProtocol(String protocolName) {
        if (protocolName == null) {
            return protocols.get("WIFI"); // Default protocol
        }
        return protocols.get(protocolName.toUpperCase());
    }
    
    /**
     * Get all available protocol names
     * 
     * @return Array of protocol names
     */
    public static String[] getAvailableProtocols() {
        return protocols.keySet().toArray(new String[0]);
    }
}
