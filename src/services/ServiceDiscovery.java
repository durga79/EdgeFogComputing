package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import models.EdgeNode;

/**
 * Service discovery mechanism for Edge-Fog computing environments
 * Addresses the "No service discovery" gap identified in the feedback
 */
public class ServiceDiscovery {
    
    // Registry of available services
    private Map<String, List<ServiceRegistration>> serviceRegistry;
    
    public ServiceDiscovery() {
        this.serviceRegistry = new HashMap<>();
    }
    
    /**
     * Register a service with the discovery mechanism
     * 
     * @param serviceId Unique identifier for the service
     * @param serviceName Human-readable name of the service
     * @param serviceType Type of service (e.g., "PROCESSING", "STORAGE", "ANALYTICS")
     * @param provider Edge node providing the service
     * @param metadata Additional service metadata
     * @return True if registration was successful
     */
    public boolean registerService(String serviceId, String serviceName, String serviceType, 
                                  EdgeNode provider, Map<String, Object> metadata) {
        ServiceRegistration registration = new ServiceRegistration(
            serviceId, serviceName, serviceType, provider, metadata
        );
        
        if (!serviceRegistry.containsKey(serviceType)) {
            serviceRegistry.put(serviceType, new ArrayList<>());
        }
        
        // Check if service already exists
        for (ServiceRegistration existing : serviceRegistry.get(serviceType)) {
            if (existing.getServiceId().equals(serviceId)) {
                // Update existing registration
                serviceRegistry.get(serviceType).remove(existing);
                serviceRegistry.get(serviceType).add(registration);
                return true;
            }
        }
        
        // Add new registration
        serviceRegistry.get(serviceType).add(registration);
        return true;
    }
    
    /**
     * Unregister a service
     * 
     * @param serviceId ID of the service to unregister
     * @return True if service was found and unregistered
     */
    public boolean unregisterService(String serviceId) {
        for (String serviceType : serviceRegistry.keySet()) {
            List<ServiceRegistration> services = serviceRegistry.get(serviceType);
            for (ServiceRegistration service : new ArrayList<>(services)) {
                if (service.getServiceId().equals(serviceId)) {
                    services.remove(service);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Find services by type
     * 
     * @param serviceType Type of service to find
     * @return List of matching service registrations
     */
    public List<ServiceRegistration> findServicesByType(String serviceType) {
        return serviceRegistry.getOrDefault(serviceType, new ArrayList<>());
    }
    
    /**
     * Find services by custom criteria
     * 
     * @param filter Predicate to filter services
     * @return List of matching service registrations
     */
    public List<ServiceRegistration> findServices(Predicate<ServiceRegistration> filter) {
        List<ServiceRegistration> results = new ArrayList<>();
        
        for (List<ServiceRegistration> services : serviceRegistry.values()) {
            for (ServiceRegistration service : services) {
                if (filter.test(service)) {
                    results.add(service);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Get all registered services
     * 
     * @return List of all service registrations
     */
    public List<ServiceRegistration> getAllServices() {
        List<ServiceRegistration> allServices = new ArrayList<>();
        
        for (List<ServiceRegistration> services : serviceRegistry.values()) {
            allServices.addAll(services);
        }
        
        return allServices;
    }
    
    /**
     * Get the number of registered services
     * 
     * @return Count of registered services
     */
    public int getServiceCount() {
        int count = 0;
        for (List<ServiceRegistration> services : serviceRegistry.values()) {
            count += services.size();
        }
        return count;
    }
    
    /**
     * Inner class representing a service registration
     */
    public static class ServiceRegistration {
        private String serviceId;
        private String serviceName;
        private String serviceType;
        private EdgeNode provider;
        private Map<String, Object> metadata;
        private long registrationTime;
        
        public ServiceRegistration(String serviceId, String serviceName, String serviceType, 
                                  EdgeNode provider, Map<String, Object> metadata) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceType = serviceType;
            this.provider = provider;
            this.metadata = metadata != null ? metadata : new HashMap<>();
            this.registrationTime = System.currentTimeMillis();
        }
        
        public String getServiceId() {
            return serviceId;
        }
        
        public String getServiceName() {
            return serviceName;
        }
        
        public String getServiceType() {
            return serviceType;
        }
        
        public EdgeNode getProvider() {
            return provider;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
        
        public long getRegistrationTime() {
            return registrationTime;
        }
        
        @Override
        public String toString() {
            return "Service[id=" + serviceId + ", name=" + serviceName + 
                   ", type=" + serviceType + ", provider=" + provider.getId() + "]";
        }
    }
}
