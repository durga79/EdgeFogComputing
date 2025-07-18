package fuzzy_logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Fuzzy Logic Controller for task offloading decisions in Edge-Cloud environments.
 * Based on the research paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
 */
public class FuzzyLogicController {
    
    // Fuzzy input variables
    private double cpuDemand;          // Task CPU demand
    private double networkDemand;      // Task network demand
    private double delaySensitivity;   // Task delay sensitivity
    private double edgeUtilization;    // Edge node CPU utilization
    private int resourceType;          // Edge node resource type (1=low, 2=high)
    
    // Fuzzy output
    private String offloadingDecision; // LOCAL_EDGE, OTHER_EDGE, or CLOUD
    
    // Membership function values
    private Map<String, Double> membershipValues;
    
    public FuzzyLogicController() {
        membershipValues = new HashMap<>();
    }
    
    /**
     * Make offloading decision based on fuzzy logic
     * 
     * @param cpuDemand Task CPU demand (MI)
     * @param networkDemand Task network demand (KB)
     * @param delaySensitivity Task delay sensitivity (0-1)
     * @param edgeUtilization Edge node CPU utilization (0-100%)
     * @param resourceType Edge node resource type (1=low, 2=high)
     * @return Offloading decision (LOCAL_EDGE, OTHER_EDGE, or CLOUD)
     */
    public String makeOffloadingDecision(double cpuDemand, double networkDemand, 
                                        double delaySensitivity, double edgeUtilization, 
                                        int resourceType) {
        this.cpuDemand = cpuDemand;
        this.networkDemand = networkDemand;
        this.delaySensitivity = delaySensitivity;
        this.edgeUtilization = edgeUtilization;
        this.resourceType = resourceType;
        
        // Step 1: Fuzzification - Calculate membership values
        fuzzify();
        
        // Step 2: Apply fuzzy rules
        applyRules();
        
        // Step 3: Defuzzification - Determine final decision
        defuzzify();
        
        return offloadingDecision;
    }
    
    /**
     * Fuzzify input variables to determine membership values
     */
    private void fuzzify() {
        // CPU Demand fuzzification (Low, Medium, High)
        membershipValues.put("cpuDemand_Low", calculateCpuDemandLow());
        membershipValues.put("cpuDemand_Medium", calculateCpuDemandMedium());
        membershipValues.put("cpuDemand_High", calculateCpuDemandHigh());
        
        // Network Demand fuzzification (Low, Medium, High)
        membershipValues.put("networkDemand_Low", calculateNetworkDemandLow());
        membershipValues.put("networkDemand_Medium", calculateNetworkDemandMedium());
        membershipValues.put("networkDemand_High", calculateNetworkDemandHigh());
        
        // Delay Sensitivity fuzzification (Low, Medium, High)
        membershipValues.put("delaySensitivity_Low", calculateDelaySensitivityLow());
        membershipValues.put("delaySensitivity_Medium", calculateDelaySensitivityMedium());
        membershipValues.put("delaySensitivity_High", calculateDelaySensitivityHigh());
        
        // Edge Utilization fuzzification (Low, Medium, High)
        membershipValues.put("edgeUtilization_Low", calculateEdgeUtilizationLow());
        membershipValues.put("edgeUtilization_Medium", calculateEdgeUtilizationMedium());
        membershipValues.put("edgeUtilization_High", calculateEdgeUtilizationHigh());
    }
    
    /**
     * Apply fuzzy rules to determine offloading decision
     */
    private void applyRules() {
        // Initialize decision weights
        double localEdgeWeight = 0.0;
        double otherEdgeWeight = 0.0;
        double cloudWeight = 0.0;
        
        // Rule 1: If CPU demand is LOW and Edge utilization is LOW, then offload to LOCAL_EDGE
        double rule1 = Math.min(membershipValues.get("cpuDemand_Low"), membershipValues.get("edgeUtilization_Low"));
        localEdgeWeight = Math.max(localEdgeWeight, rule1);
        
        // Rule 2: If CPU demand is HIGH and Edge utilization is HIGH and Delay sensitivity is LOW, then offload to CLOUD
        double rule2 = Math.min(Math.min(membershipValues.get("cpuDemand_High"), 
                                        membershipValues.get("edgeUtilization_High")),
                                membershipValues.get("delaySensitivity_Low"));
        cloudWeight = Math.max(cloudWeight, rule2);
        
        // Rule 3: If CPU demand is MEDIUM and Edge utilization is HIGH and Delay sensitivity is HIGH, then offload to OTHER_EDGE
        double rule3 = Math.min(Math.min(membershipValues.get("cpuDemand_Medium"), 
                                        membershipValues.get("edgeUtilization_High")),
                                membershipValues.get("delaySensitivity_High"));
        otherEdgeWeight = Math.max(otherEdgeWeight, rule3);
        
        // Rule 4: If Network demand is HIGH and Delay sensitivity is HIGH, then offload to LOCAL_EDGE
        double rule4 = Math.min(membershipValues.get("networkDemand_High"), 
                               membershipValues.get("delaySensitivity_High"));
        localEdgeWeight = Math.max(localEdgeWeight, rule4);
        
        // Rule 5: If CPU demand is HIGH and Edge utilization is LOW and resource type is HIGH, then offload to LOCAL_EDGE
        if (resourceType == 2) { // High resource type
            double rule5 = Math.min(membershipValues.get("cpuDemand_High"), 
                                   membershipValues.get("edgeUtilization_Low"));
            localEdgeWeight = Math.max(localEdgeWeight, rule5);
        }
        
        // Rule 6: If CPU demand is HIGH and Edge utilization is MEDIUM and resource type is LOW, then offload to OTHER_EDGE
        if (resourceType == 1) { // Low resource type
            double rule6 = Math.min(membershipValues.get("cpuDemand_High"), 
                                   membershipValues.get("edgeUtilization_Medium"));
            otherEdgeWeight = Math.max(otherEdgeWeight, rule6);
        }
        
        // Store weights for defuzzification
        membershipValues.put("localEdgeWeight", localEdgeWeight);
        membershipValues.put("otherEdgeWeight", otherEdgeWeight);
        membershipValues.put("cloudWeight", cloudWeight);
    }
    
    /**
     * Defuzzify to determine final offloading decision
     */
    private void defuzzify() {
        double localEdgeWeight = membershipValues.get("localEdgeWeight");
        double otherEdgeWeight = membershipValues.get("otherEdgeWeight");
        double cloudWeight = membershipValues.get("cloudWeight");
        
        // Find the decision with the highest weight
        if (localEdgeWeight >= otherEdgeWeight && localEdgeWeight >= cloudWeight) {
            offloadingDecision = "LOCAL_EDGE";
        } else if (otherEdgeWeight >= localEdgeWeight && otherEdgeWeight >= cloudWeight) {
            offloadingDecision = "OTHER_EDGE";
        } else {
            offloadingDecision = "CLOUD";
        }
    }
    
    // Membership function calculations for CPU Demand
    private double calculateCpuDemandLow() {
        if (cpuDemand <= 3000) return 1.0;
        if (cpuDemand >= 6000) return 0.0;
        return (6000 - cpuDemand) / 3000.0;
    }
    
    private double calculateCpuDemandMedium() {
        if (cpuDemand <= 3000 || cpuDemand >= 10000) return 0.0;
        if (cpuDemand >= 6000 && cpuDemand <= 8000) return 1.0;
        if (cpuDemand > 3000 && cpuDemand < 6000) return (cpuDemand - 3000) / 3000.0;
        return (10000 - cpuDemand) / 2000.0;
    }
    
    private double calculateCpuDemandHigh() {
        if (cpuDemand <= 8000) return 0.0;
        if (cpuDemand >= 15000) return 1.0;
        return (cpuDemand - 8000) / 7000.0;
    }
    
    // Membership function calculations for Network Demand
    private double calculateNetworkDemandLow() {
        if (networkDemand <= 1500) return 1.0;
        if (networkDemand >= 2500) return 0.0;
        return (2500 - networkDemand) / 1000.0;
    }
    
    private double calculateNetworkDemandMedium() {
        if (networkDemand <= 1500 || networkDemand >= 3500) return 0.0;
        if (networkDemand >= 2500 && networkDemand <= 3000) return 1.0;
        if (networkDemand > 1500 && networkDemand < 2500) return (networkDemand - 1500) / 1000.0;
        return (3500 - networkDemand) / 500.0;
    }
    
    private double calculateNetworkDemandHigh() {
        if (networkDemand <= 3000) return 0.0;
        if (networkDemand >= 5000) return 1.0;
        return (networkDemand - 3000) / 2000.0;
    }
    
    // Membership function calculations for Delay Sensitivity
    private double calculateDelaySensitivityLow() {
        if (delaySensitivity <= 0.1) return 1.0;
        if (delaySensitivity >= 0.5) return 0.0;
        return (0.5 - delaySensitivity) / 0.4;
    }
    
    private double calculateDelaySensitivityMedium() {
        if (delaySensitivity <= 0.1 || delaySensitivity >= 0.9) return 0.0;
        if (delaySensitivity >= 0.5 && delaySensitivity <= 0.7) return 1.0;
        if (delaySensitivity > 0.1 && delaySensitivity < 0.5) return (delaySensitivity - 0.1) / 0.4;
        return (0.9 - delaySensitivity) / 0.2;
    }
    
    private double calculateDelaySensitivityHigh() {
        if (delaySensitivity <= 0.7) return 0.0;
        if (delaySensitivity >= 0.9) return 1.0;
        return (delaySensitivity - 0.7) / 0.2;
    }
    
    // Membership function calculations for Edge Utilization
    private double calculateEdgeUtilizationLow() {
        if (edgeUtilization <= 30) return 1.0;
        if (edgeUtilization >= 50) return 0.0;
        return (50 - edgeUtilization) / 20.0;
    }
    
    private double calculateEdgeUtilizationMedium() {
        if (edgeUtilization <= 30 || edgeUtilization >= 80) return 0.0;
        if (edgeUtilization >= 50 && edgeUtilization <= 70) return 1.0;
        if (edgeUtilization > 30 && edgeUtilization < 50) return (edgeUtilization - 30) / 20.0;
        return (80 - edgeUtilization) / 10.0;
    }
    
    private double calculateEdgeUtilizationHigh() {
        if (edgeUtilization <= 70) return 0.0;
        if (edgeUtilization >= 90) return 1.0;
        return (edgeUtilization - 70) / 20.0;
    }
}
