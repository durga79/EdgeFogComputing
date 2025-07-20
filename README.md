# Edge-Fog Computing Task Offloading System

This project implements a proof-of-concept for IoT task offloading in edge-cloud environments based on the research paper:
"A novel approach for IoT tasks offloading in edge-cloud environments" (2021) from the Journal of Cloud Computing.

## Project Overview

This project implements a comprehensive task offloading system for Edge-Fog computing environments focused on IoT applications. The system features a three-tier architecture comprising IoT devices, edge nodes, and cloud resources, with intelligent task offloading decisions to minimize service time for latency-sensitive applications while efficiently utilizing heterogeneous resources.

## System Architecture

The system consists of three main layers:
1. **IoT Devices Layer**: Simulated IoT devices generating tasks with varying computational and communication demands
2. **Edge Computing Layer**: Multiple edge nodes with heterogeneous resources
3. **Cloud Computing Layer**: Centralized cloud resources

## Key Components

The implementation includes the following key components:

1. **Wireless Protocols**:
   - WirelessProtocol interface
   - LoRaWAN, NB-IoT, 5G, and WiFi protocol implementations
   - WirelessProtocolFactory for managing protocol instances

2. **Big Data Analytics**:
   - DataProcessor interface
   - StreamingAnalytics for real-time data processing
   - DataAggregator for edge data aggregation

3. **Advanced Service Management**:
   - ServiceDiscovery for service registration and discovery
   - ServiceMigration for dynamic service migration
   - FaultTolerance for handling node failures

4. **Energy Efficiency**:
   - EnergyModel interface
   - LinearEnergyModel for energy consumption calculations
   - BatteryModel for IoT device battery simulation

5. **Security Mechanisms**:
   - SecurityManager for authentication and authorization
   - SecureCommunication for encrypted data exchange
   - SecureOffloading for secure task offloading

## Project Structure

```
EdgeFogComputing/
├── bin/                  # Compiled class files (generated during build)
├── src/                  # Source code
│   ├── bigdata/          # Big data analytics components
│   ├── energy/           # Energy models and battery simulation
│   ├── models/           # Core system models (EdgeNode, IoTDevice, Task, etc.)
│   ├── network/          # Wireless protocols implementation
│   ├── security/         # Security mechanisms
│   ├── services/         # Service discovery and migration
│   ├── simulation/       # Simulation framework and main classes
│   └── test/             # Test classes
├── results/              # Simulation results (CSV files)
├── compile_and_run.sh    # Script to compile and run tests
└── README.md             # This file
```

## Testing Instructions

Follow these steps to test the Edge-Fog Computing system:

### Prerequisites
- Java JDK 8 or higher

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd EdgeFogComputing
```

### Step 2: Create Required Directories
```bash
mkdir -p bin
mkdir -p results
```

### Step 3: Compile and Run Tests
The easiest way is to use the provided script:
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

This script will:
1. Create the bin directory if it doesn't exist
2. Clean any previous compilation
3. Compile all Java files
4. Run the test classes in sequence:
   - TestSimulation (basic component testing)
   - SimpleEdgeNodeTest (focused EdgeNode testing)
   - TestEdgeFogComponents (comprehensive component testing)
   - Full EdgeFogSimulation (complete system simulation)

### Step 4: Review Test Results
After running the tests, check:
- Console output for any errors
- The results directory for CSV files with simulation statistics:
  - simulation_results.csv
  - advanced_results.csv
  - protocol_usage.csv

### Step 5: Modify and Extend (Optional)
If you want to modify or extend the system:
1. Make changes to the source files in the src directory
2. Run the compile_and_run.sh script again to test the changes

## Important Notes

- The bin directory with .class files is automatically generated during compilation and should be excluded from version control
- The results directory will be created during simulation runs
- All source code is in the src directory organized by packages

## Evaluation Metrics

The system measures and reports the following metrics:

- **Performance Metrics**:
  - Average service time (Local Edge, Other Edge, Cloud)
  - Resource utilization (Edge nodes, Cloud)

- **Energy Efficiency**:
  - Total energy consumed
  - Average battery level
  - Low battery devices count

- **Security Performance**:
  - Security incidents detected

- **Service Management**:
  - Successful migrations
  - Failed migrations
  - Recovered tasks
  - Registered services

- **Wireless Protocol Distribution**:
  - Protocol usage statistics

## References

1. Original research paper: "A novel approach for IoT tasks offloading in edge-cloud environments" (2021)

