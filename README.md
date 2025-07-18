# Edge-Fog Computing Task Offloading System

This project implements a proof-of-concept for IoT task offloading in edge-cloud environments based on the research paper:
"A novel approach for IoT tasks offloading in edge-cloud environments" (2021) from the Journal of Cloud Computing.

## Project Overview
This project implements a proof-of-concept for a task offloading system in Edge-Fog computing environments for IoT applications. It is based on the research paper "A novel approach for IoT tasks offloading in edge-cloud environments" (2021).

The implementation focuses on using fuzzy logic to make intelligent task offloading decisions in a three-tier architecture comprising IoT devices, edge nodes, and cloud resources. The goal is to minimize service time for latency-sensitive applications while efficiently utilizing heterogeneous resources.

## System Architecture

The system consists of three main layers:
1. **IoT Devices Layer**: Simulated IoT devices generating tasks with varying computational and communication demands
2. **Edge Computing Layer**: Multiple edge nodes with heterogeneous resources
3. **Cloud Computing Layer**: Centralized cloud resources

The key component is a fuzzy logic controller that makes offloading decisions based on:
- Task characteristics (CPU demand, network demand, delay sensitivity)
- Infrastructure conditions (resource utilization, resource heterogeneity)

## Implementation Details

The implementation uses EdgeCloudSim, a simulation environment for edge computing scenarios. The key components include:

- Task generation module
- Fuzzy logic-based offloading decision system
- Resource management system
- Performance evaluation metrics

## Project Structure

```
EdgeFogComputing/
├── src/                  # Source code
│   ├── fuzzy_logic/      # Fuzzy logic implementation
│   ├── models/           # System models (IoT, Edge, Cloud)
│   └── utils/            # Utility functions
├── simulation/           # Simulation configurations
├── docs/                 # Documentation
└── results/              # Simulation results and analysis
```

## Setup Instructions

### Prerequisites
- Java JDK 11 or higher
- Maven 3.6.0 or higher (for dependency management)
- EdgeCloudSim
- Fuzzy logic library

### Installation
1. Clone the repository
2. Install dependencies
3. Configure simulation parameters
4. Run the simulation

## Evaluation Metrics

- Service time
- Resource utilization
- Network usage
- Energy consumption

## References

1. Original research paper: "A novel approach for IoT tasks offloading in edge-cloud environments" (2021)
2. EdgeCloudSim: https://github.com/CagataySonmez/EdgeCloudSim
# EdgeFogComputing
