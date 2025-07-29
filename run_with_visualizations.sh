#!/bin/bash

echo "Edge-Fog Computing - Direct Visualization Runner"
echo "==============================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java first."
    echo "You can run: sudo apt update && sudo apt install -y openjdk-11-jdk"
    exit 1
fi

# Create simulation_results directory if it doesn't exist
mkdir -p simulation_results

# Build the project using Maven
echo "Building the project with Maven..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Failed to build the project. Please check for errors."
    exit 1
fi

# Determine which simulation to run
SIMULATION="cloudsim"
if [ "$1" == "ifogsim" ] || [ "$1" == "ifog" ] || [ "$1" == "fog" ]; then
    SIMULATION="ifogsim"
    echo "Running iFogSim simulation with direct visualizations..."
else
    echo "Running CloudSim simulation with direct visualizations..."
fi

# Run the visualization-enabled simulation
echo "Starting simulation with direct visualization charts..."
java -cp target/edge-fog-computing-1.0-jar-with-dependencies.jar visualization.RunWithVisualization $SIMULATION

echo "Simulation complete!"
echo "You can now view the generated chart PNG files directly in VS Code."
echo "Look for the newest folder in simulation_results/ directory."
