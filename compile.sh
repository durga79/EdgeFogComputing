#!/bin/bash

echo "Edge-Fog Computing Task Offloading System - Manual Compilation"
echo "==========================================================="

# Create output directories
mkdir -p bin
mkdir -p results

# Compile the source files
echo "Compiling Java source files..."
javac -d bin src/models/*.java src/fuzzy_logic/*.java src/simulation/*.java src/visualization/*.java src/Main.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the simulation, use: java -cp bin Main"
else
    echo "Compilation failed. Please check the error messages above."
fi
