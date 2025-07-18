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
mvn clean install

echo "Setup complete!"
echo "To run the simulation, use: java -jar target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar"
