# Edge-Fog Computing System Architecture

```
                                 +----------------------------------+
                                 |          Cloud Layer             |
                                 |  +---------------------------+   |
                                 |  |      Cloud Server         |   |
                                 |  | - High Computational Power|   |
                                 |  | - Large Storage Capacity  |   |
                                 |  | - High Latency Access     |   |
                                 |  +---------------------------+   |
                                 +----------------^----------------+
                                                  |
                                                  | WAN Connection
                                                  | (High Bandwidth, High Latency)
                                                  |
                  +-------------------------------v--------------------------------+
                  |                          Edge Layer                            |
                  |                                                                |
                  |  +------------------------+        +----------------------+    |
                  |  |    Edge Controller     |<------>| Fuzzy Logic Controller|   |
                  |  | - Orchestration        |        | - Task Offloading     |   |
                  |  | - Resource Management  |        |   Decision Making     |   |
                  |  +----------^-------------+        +----------------------+    |
                  |             |                                                  |
                  |             |                                                  |
          +-------+-------------+----------+                                       |
          |                |               |                                       |
+---------v---------+ +----v----------+ +--v-------------+                         |
|   Edge Node 1     | |  Edge Node 2  | |  Edge Node 3   |                         |
| - Type 1 (Low)    | | - Type 2 (High)| | - Type 1 (Low) |                         |
| - Local Processing| | - Local       | | - Local        |                         |
|   Capability      | |   Processing   | |   Processing    |                         |
+--------^----------+ +----^----------+ +--^-------------+                         |
         |                |               |                                        |
         +----------------+---------------+                                        |
                          |                                                        |
                          | WLAN Connection                                        |
                          | (Medium Bandwidth, Low Latency)                        |
                          |                                                        |
+-------------------------v--------------------------------------------------------+
|                                    IoT Layer                                     |
|                                                                                 |
|  +---------------+  +---------------+  +---------------+  +---------------+     |
|  |  IoT Device   |  |  IoT Device   |  |  IoT Device   |  |  IoT Device   |     |
|  |    Type 1     |  |    Type 2     |  |    Type 3     |  |    Type 4     |     |
|  | - Low CPU     |  | - Medium CPU  |  | - High CPU    |  | - Very High   |     |
|  | - Low Network |  | - Medium Net  |  | - Medium Net  |  |   CPU         |     |
|  | - High Delay  |  | - Medium Delay|  | - Medium Delay|  | - High Network|     |
|  |   Sensitivity |  |   Sensitivity |  |   Sensitivity |  | - Low Delay   |     |
|  |               |  |               |  |               |  |   Sensitivity |     |
|  +---------------+  +---------------+  +---------------+  +---------------+     |
|                                                                                 |
|                         ... (Multiple IoT Devices) ...                          |
|                                                                                 |
+----------------------------------------------------------------------------------+
```

## Key Components

1. **IoT Layer**:
   - Multiple IoT devices generating tasks with varying characteristics
   - Different application profiles with varying CPU demand, network demand, and delay sensitivity
   - Mobile devices that connect to the nearest edge node

2. **Edge Layer**:
   - Multiple edge nodes with heterogeneous resources (Type 1: Low, Type 2: High)
   - Edge Controller for orchestrating task distribution
   - Fuzzy Logic Controller for making intelligent offloading decisions

3. **Cloud Layer**:
   - Centralized cloud server with high computational power
   - Connected to edge nodes via WAN with higher latency

## Data Flow

1. IoT devices generate tasks with specific requirements
2. Tasks are sent to the connected edge node
3. Edge Controller uses Fuzzy Logic to decide where to process each task:
   - LOCAL_EDGE: Process on the connected edge node
   - OTHER_EDGE: Process on another edge node with lower utilization
   - CLOUD: Process on the cloud server
4. Tasks are executed at the selected location
5. Performance metrics are collected and analyzed
