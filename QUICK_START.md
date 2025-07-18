# Edge-Fog Computing Task Offloading System - Quick Start Guide

This guide will help you get the project up and running once the necessary dependencies are installed.

## Prerequisites

- Java JDK 11 or higher
- Maven 3.6.0 or higher (optional, but recommended)

## Installation Steps

### 1. Install Java

```bash
sudo apt update
sudo apt install -y openjdk-11-jdk
```

Verify the installation:
```bash
java -version
```

### 2. Install Maven (Optional but Recommended)

```bash
sudo apt install -y maven
```

Verify the installation:
```bash
mvn -version
```

## Running the Project

### Option 1: Using the Automated Scripts

We've provided several scripts to make it easy to run the project:

1. **Setup and Build**:
   ```bash
   ./setup.sh
   ```
   This will install dependencies (if needed) and build the project.

2. **Run the Simulation**:
   ```bash
   ./run_simulation.sh
   ```
   This will run the full simulation and offer to visualize the results.

3. **Test Individual Components**:
   ```bash
   ./test_components.sh
   ```
   This interactive script allows you to test specific components of the system.

### Option 2: Manual Build and Run

If you prefer to build and run the project manually:

#### Using Maven:

1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the simulation:
   ```bash
   java -jar target/edge-fog-computing-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

#### Without Maven:

1. Create the bin directory:
   ```bash
   mkdir -p bin
   ```

2. Compile the project:
   ```bash
   javac -d bin src/models/*.java src/fuzzy_logic/*.java src/simulation/*.java src/visualization/*.java src/Main.java
   ```

3. Run the simulation:
   ```bash
   java -cp bin Main
   ```

## Viewing Results

After running the simulation, results will be saved to the `results/` directory. You can visualize these results using:

```bash
java -cp bin visualization.ResultsVisualizer results/simulation_results.csv
```

## Troubleshooting

1. **Compilation Errors**:
   - Ensure Java is properly installed
   - Check that all source files are in the correct package structure
   - Verify that there are no syntax errors in the code

2. **Runtime Errors**:
   - Check the configuration file (`simulation/default_config.properties`)
   - Ensure all required directories exist
   - Look for detailed error messages in the console output

3. **Visualization Issues**:
   - Verify that the results file exists
   - Ensure the JFreeChart library is available (when using Maven)

## Project Structure

```
EdgeFogComputing/
├── src/                  # Source code
│   ├── fuzzy_logic/      # Fuzzy logic implementation
│   ├── models/           # System models (IoT, Edge, Cloud)
│   ├── simulation/       # Simulation engine
│   └── visualization/    # Results visualization
├── simulation/           # Simulation configurations
├── docs/                 # Documentation
└── results/              # For simulation results
```

For more detailed information, please refer to the README.md and the project report in the docs directory.
