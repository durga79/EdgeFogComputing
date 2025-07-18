#!/bin/bash

echo "Edge-Fog Computing Task Offloading System - Simulation Runner"
echo "=========================================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java first."
    echo "You can run: sudo apt update && sudo apt install -y openjdk-11-jdk"
    exit 1
fi

# Check if the project is compiled
if [ ! -d "bin" ] || [ ! -f "bin/Main.class" ]; then
    echo "Project is not compiled. Attempting to compile..."
    
    # Check if Maven is installed
    if command -v mvn &> /dev/null; then
        echo "Using Maven to build the project..."
        mvn clean install
        
        if [ $? -eq 0 ]; then
            echo "Maven build successful. Running with Maven-built JAR..."
            java -jar target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar
            exit $?
        else
            echo "Maven build failed. Trying manual compilation..."
        fi
    fi
    
    # Manual compilation fallback
    echo "Compiling manually..."
    mkdir -p bin
    javac -d bin src/models/*.java src/fuzzy_logic/*.java src/simulation/*.java src/visualization/*.java src/Main.java
    
    if [ $? -ne 0 ]; then
        echo "Compilation failed. Please check the error messages above."
        exit 1
    fi
fi

# Create results directory if it doesn't exist
mkdir -p results

# Run the simulation
echo "Starting simulation..."
if [ -f "target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar" ]; then
    java -jar target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar
else
    java -cp bin Main
fi

# Check if the simulation completed successfully
if [ $? -eq 0 ]; then
    echo "Simulation completed successfully!"
    echo "Results are available in the 'results' directory."
    
    # Check if we can visualize results
    if [ -f "results/simulation_results.csv" ]; then
        echo "Would you like to visualize the results? (y/n)"
        read -r response
        if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
            echo "Launching visualization..."
            java -cp bin visualization.ResultsVisualizer results/simulation_results.csv
        fi
    else
        echo "No results file found. Visualization is not available."
    fi
else
    echo "Simulation failed with exit code $?."
    echo "Please check the error messages above."
fi
