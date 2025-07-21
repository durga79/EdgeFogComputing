#!/bin/bash

echo "Edge-Fog Computing Task Offloading System Setup"
echo "=============================================="

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Java not found. Installing OpenJDK 11..."
    sudo apt update
    sudo apt install -y openjdk-11-jdk
else
    echo "Java is already installed:"
    java -version
fi

# Check for Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Installing Maven..."
    sudo apt update
    sudo apt install -y maven
else
    echo "Maven is already installed:"
    mvn -version
fi

# Create directories if they don't exist
mkdir -p results

echo "Setting up project structure..."
# Compile the project
echo "Compiling project..."
mvn clean package

echo "Setup complete!"
echo "
Usage Options:"
echo "============"
echo "1. Run with SimulationLauncher (recommended):"
echo "   java -cp target/edge-fog-computing-1.0.jar SimulationLauncher [options]"
echo "
Available options:"
echo "  --type <framework>     : Simulation framework to use (custom, cloudsim, ifogsim)"
echo "  --edge-nodes <number>  : Number of edge nodes to simulate"
echo "  --iot-devices <number> : Number of IoT devices to simulate"
echo "  --time <seconds>       : Simulation duration in seconds"
echo "  --basic                : Disable advanced features"
echo "
Examples:"
echo "  # Run with CloudSim framework:"
echo "  java -cp target/edge-fog-computing-1.0.jar SimulationLauncher --type cloudsim"
echo "
  # Run with iFogSim framework and custom parameters:"
echo "  java -cp target/edge-fog-computing-1.0.jar SimulationLauncher --type ifogsim --edge-nodes 10 --iot-devices 100"
echo "
2. Run specific implementations directly:"
echo "   # Custom implementation:"
echo "   java -cp target/edge-fog-computing-1.0.jar Main"
echo "   # CloudSim implementation:"
echo "   java -cp target/edge-fog-computing-1.0.jar cloudsim_integration.CloudSimMain"
echo "   # iFogSim implementation:"
echo "   java -cp target/edge-fog-computing-1.0.jar ifogsim_integration.IFogSimMain"
