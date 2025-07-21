# Edge-Fog Computing Task Offloading System

This project implements a proof-of-concept for IoT task offloading in edge-cloud environments based on the research paper:
"A novel approach for IoT tasks offloading in edge-cloud environments" (2021) from the Journal of Cloud Computing.

## Project Overview

This project implements a comprehensive task offloading system for Edge-Fog computing environments focused on IoT applications. The system features a three-tier architecture comprising IoT devices, edge nodes, and cloud resources, with intelligent task offloading decisions to minimize service time for latency-sensitive applications while efficiently utilizing heterogeneous resources.

The implementation provides three different simulation frameworks:
1. **Custom Simulation Framework**: A custom-built simulation framework
2. **CloudSim Integration**: Implementation using CloudSim stub classes (no external dependencies)
3. **iFogSim Integration**: Implementation using iFogSim stub classes (no external dependencies)

> **Note**: This project uses stub implementations for CloudSim and iFogSim instead of external JAR dependencies, making it self-contained and easy to set up.

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
│   ├── cloudsim_integration/ # CloudSim integration components
│   ├── energy/           # Energy models and battery simulation
│   ├── ifogsim_integration/ # iFogSim integration components
│   ├── models/           # Core system models (EdgeNode, IoTDevice, Task, etc.)
│   ├── network/          # Wireless protocols implementation
│   ├── security/         # Security mechanisms
│   ├── services/         # Service discovery and migration
│   ├── simulation/       # Custom simulation framework and main classes
│   └── test/             # Test classes
├── results/              # Simulation results (CSV files)
├── compile_and_run.sh    # Script to compile and run tests
├── pom.xml               # Maven project configuration
└── README.md             # This file
```

## Setup and Testing Instructions

Follow these steps to set up and test the Edge-Fog Computing system:

### Prerequisites
- Java JDK 11 or higher
- Apache Maven 3.6.3 or higher

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd EdgeFogComputing
```

### Step 2: Build the Project
The project uses Maven for dependency management and building:

```bash
# Compile the project
mvn clean compile

# Package the project (optional)
mvn clean package
```

### Step 3: Run the Simulations
You can run the simulations directly using the compiled classes:

```bash
# Run CloudSim simulation
java -cp target/classes cloudsim_integration.CloudSimMain

# Run iFogSim simulation
java -cp target/classes ifogsim_integration.IFogSimMain
```

Or if you've packaged the project:

```bash
# Run CloudSim simulation
java -cp target/edge-fog-computing-1.0-jar-with-dependencies.jar cloudsim_integration.CloudSimMain

# Run iFogSim simulation
java -cp target/edge-fog-computing-1.0-jar-with-dependencies.jar ifogsim_integration.IFogSimMain
```

### Step 4: Expected Output

When running the CloudSim simulation, you should see output similar to:

```
Edge-Fog Computing Task Offloading System using CloudSim
Based on the research paper: "A novel approach for IoT tasks offloading in edge-cloud environments"
by Jaber Almutairi and Mohammad Aldossary

Simulation Configuration:
- Number of Edge Nodes: 5
- Number of IoT Devices: 50
- Simulation Time: 30.0
- Advanced Features: Enabled

Initializing CloudSim simulation...
[CloudSim] Initialized with 1 users
[CloudSim] Created datacenter: CloudDatacenter
[CloudSim] Created datacenter: EdgeDatacenter_0
...
CloudSim simulation completed successfully.
```

When running the iFogSim simulation, you should see output similar to:

```
Edge-Fog Computing Task Offloading System using iFogSim
...
Initializing iFogSim simulation...
[CloudSim] Initialized with 1 users
[iFogSim] Created FogDevice: Cloud
[iFogSim] Created FogDevice: Edge-0
...
iFogSim simulation completed successfully.
```

The simulation will:
1. Initialize the selected simulation framework
2. Create edge nodes, cloud resources, and IoT devices
3. Run the simulation with task offloading logic
4. Report performance metrics and statistics

### Step 5: Troubleshooting

If you encounter any issues:

1. **Compilation errors**: Ensure you have Java 11 installed and properly configured
   ```bash
   java -version
   ```

2. **Missing dependencies**: Check if Maven is properly downloading dependencies
   ```bash
   mvn dependency:tree
   ```

3. **Runtime errors**: Check for any exceptions in the console output and verify that all stub classes are properly implemented

4. **ClassNotFoundException**: Ensure you're using the correct classpath when running the application

### Step 6: Extending the Project

To extend the project with new features:

1. **Add new IoT device types**: Extend the `IoTDevice` class
2. **Implement new wireless protocols**: Add new classes implementing the `WirelessProtocol` interface
3. **Create new energy models**: Implement the `EnergyModel` interface
4. **Add security mechanisms**: Extend the security package
5. **Enhance data analytics**: Add new components to the bigdata package

## Important Notes

- This project uses **stub implementations** for CloudSim and iFogSim instead of external dependencies
- The simulation parameters can be modified in the respective Main classes
- The 3D spatial modeling in the `Location` class enables more realistic distance calculations
- All compiled classes are placed in the `target/classes` directory by Maven
- The project is designed to be self-contained with no external simulation framework dependencies

## Advanced Features

The project has been enhanced with several advanced features to provide a comprehensive edge-fog computing simulation:

1. **Wireless Protocols**:
   - WirelessProtocol interface with LoRaWAN, NB-IoT, 5G, and WiFi implementations
   - Protocol-specific latency, bandwidth, and energy consumption characteristics
   - WirelessProtocolFactory for dynamic protocol selection

2. **Big Data Analytics**:
   - DataProcessor interface for data processing pipelines
   - StreamingAnalytics for real-time data processing at the edge
   - DataAggregator for efficient data aggregation before cloud transmission

3. **Advanced Service Management**:
   - ServiceDiscovery for dynamic service registration and discovery
   - ServiceMigration for seamless service mobility between edge nodes
   - FaultTolerance mechanisms for handling node failures

4. **Energy Efficiency**:
   - EnergyModel interface with configurable consumption patterns
   - LinearEnergyModel for realistic energy consumption calculations
   - BatteryModel for IoT device battery simulation and management

5. **Security Mechanisms**:
   - SecurityManager for authentication and authorization
   - SecureCommunication for encrypted data exchange
   - SecureOffloading for protecting sensitive tasks during offloading

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

## Simulation Frameworks

### Custom Simulation Framework
The original custom-built simulation framework that implements all components from scratch.

### CloudSim Integration
Integration with CloudSim (Cloud Simulation) framework that provides:
- Realistic cloud datacenter modeling
- VM allocation and scheduling
- Task (cloudlet) execution simulation
- Performance metrics collection

### iFogSim Integration
Integration with iFogSim (Internet of Things, Fog and Edge Simulation) framework that provides:
- Hierarchical fog computing architecture
- Fog device modeling
- Application placement strategies
- Network topology and latency simulation
- Energy consumption modeling

## References

1. Original research paper: "A novel approach for IoT tasks offloading in edge-cloud environments" (2021)
2. CloudSim: http://www.cloudbus.org/cloudsim/
3. iFogSim: http://www.cloudbus.org/cloudsim/cloudsim-3.0.3/

