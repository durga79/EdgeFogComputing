#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

# Clean previous compilation
rm -rf bin/*

# Compile all Java files
echo "Compiling all Java files..."
javac -d bin $(find src -name "*.java")

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    echo "\n=== Running TestSimulation ===\n"
    java -cp bin simulation.TestSimulation
    
    echo "\n=== Running SimpleEdgeNodeTest ===\n"
    java -cp bin test.SimpleEdgeNodeTest
    
    echo "\n=== Running TestEdgeFogComponents ===\n"
    java -cp bin test.TestEdgeFogComponents
    
    echo "\n=== Running Full EdgeFogSimulation ===\n"
    java -cp bin simulation.EdgeFogSimulation
else
    echo "Compilation failed!"
fi
