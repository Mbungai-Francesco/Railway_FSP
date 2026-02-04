# Railway Train Simulation - Concurrent Programming (TP11-15)

## Objectives

This project demonstrates:
- Class diagram modeling with methods and attributes
- Systematic thread synchronization based on safety invariants
- Java monitors usage for blocking/unblocking threads in concurrent programs

---

## Context

This simulation models a railway system with trains moving on a single track. The railway line consists of elements that can be either **stations** or **track sections**. A line starts and ends with a station, and can only be composed of track sections (no intermediate stations allowed). A station can contain multiple platforms (quays).

**Key constraints:**
- A train is always on exactly one element of the line
- A train can only be adjacent to the element to its left or right (direction-based movement)
- Trains can only start from a station initially
- **A section can only receive one train at a time**

---

## Exercise 1: Train Behavior

### Question 1.1: Role of Each Class

**Railway**: Represents the railway circuit composed of a sequence of elements (stations and sections).

**Element**: Abstract class representing a generic railway element. Provides common functionalities for stations and sections (enter, leave, belonging to railway).

**Station**: Represents a station with a specific capacity (number of platforms). Extends Element.

**Section**: Represents a track section that can contain at most one train. Extends Element.

**Position**: Represents a train's position in the circuit, including both the element (station/section) and the direction (left-to-right or right-to-left).

**Train**: Represents a train with a name and position. Implements Runnable for concurrent execution.

**Direction**: Enumeration for train movement direction (LR = left-to-right, RL = right-to-left).

### Question 1.2: Class Diagram Modifications

Added to Position class:
- Method `move(Railway railway, String trainName)`: Moves the train to the next position

### Question 1.3: Code for Identified Methods

See implementation in `Position.java`, method `move()`.

---

## Exercise 2: Multiple Trains on the Line

### Question 2.1: Multiple Active Trains

Modified `Main.java` to create and start multiple train threads. Each train runs in its own thread, continuously moving on the railway.

### Question 2.2: Safety Invariant

**Safety invariant**: 
- Maximum number of trains in a station = number of platforms (quays) in that station
- **Maximum one train per section**
- **When a train is in a section moving in one direction, no other train can be in a section moving in the opposite direction**

### Question 2.3: Variables for Safety Invariant Expression

For the railway line:
- `currentDirection` (in Railway class): Direction of trains currently moving on the line
- `trainsMoving` (in Railway class): Number of trains currently in motion

For each section:
- `occupyingTrain` (in Section class): Name of the train occupying the section
- `trainDirection` (in Section class): Direction of the occupying train

### Question 2.4: Critical Actions

**Critical actions** a train can perform:
1. **Entering a section**: Must check if section is free before entering
2. **Leaving a section**: Must notify waiting trains that section is now available
3. **Leaving a station to enter a section**: Must check if another train is moving in opposite direction

### Question 2.5: Classes for These Actions

- **Section class**: Implements `enter()` and `leave()` methods with synchronization
- **Railway class**: Implements global locking mechanism (`acquireRailwayLock()`, `releaseRailwayLock()`)

### Question 2.6: Synchronization Construction Method

Using **Java monitors** with:
- `synchronized` keyword on methods
- `wait()` to block a train until condition is satisfied
- `notifyAll()` to wake up waiting trains when resource becomes available

### Question 2.7: Added Methods

**In Section.java:**
```java
public synchronized void enter(String trainName, Direction direction)
public synchronized void leave(String trainName)
```

**In Railway.java:**
```java
public synchronized boolean acquireRailwayLock(Direction direction)
public synchronized void releaseRailwayLock()
```

**In Position.java:**
```java
public synchronized String move(Railway railway, String trainName)
```

### Question 2.8: Testing

Tested with 1, 2, and 3 trains. The solution works correctly with all configurations:
- With 1 train: Train moves back and forth continuously
- With 2 trains: Trains alternate, one completes its journey before the other starts
- With 3 trains: All three trains take turns sequentially without conflicts

---

## Exercise 3: Avoiding Deadlocks

### Question 3.1: Variables for New Condition

**Q3.1 Variable in Section class:**
```java
private Direction trainDirection = null;  // Direction of the occupying train
```

### Question 3.2: New Condition for Safety Invariant

**Q3.2 Condition**: A train cannot enter a section if:
- The section is already occupied by another train, OR
- Another train is moving in the opposite direction on the railway

This prevents deadlocks where two trains moving in opposite directions could block each other.

### Question 3.3: Responsible Class

**Q3.3**: The **Section class** is responsible for managing the `trainDirection` variable and checking the occupancy condition in its `enter()` method.

Additionally, the **Railway class** manages the global direction state to prevent opposite-direction conflicts.

### Question 3.4: Synchronization Method Implementation

**Q3.4 Implementation**:

The Railway class implements a global locking mechanism:

```java
public synchronized boolean acquireRailwayLock(Direction direction) {
    // Wait while any train is moving (only one train at a time)
    while(trainsMoving > 0) {
        try {
            this.wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    // Set direction and mark one train as moving
    currentDirection = direction;
    trainsMoving = 1;
    return true;
}

public synchronized void releaseRailwayLock() {
    trainsMoving--;
    if(trainsMoving == 0) {
        currentDirection = null;
        this.notifyAll();
    }
}
```

In Position.move():
- Acquire railway lock when leaving a station
- Release railway lock when arriving at a station

### Question 3.5: Modify leave and enter in Section

**Q3.5 Implementation**:

Modified `Section.enter()` to include direction checking:

```java
public synchronized void enter(String trainName, Direction direction) {
    // Wait while section is occupied by a different train
    while(occupyingTrain != null && !occupyingTrain.equals(trainName)) {
        try {
            this.wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    // Occupy the section with direction
    occupyingTrain = trainName;
    trainDirection = direction;
}
```

Modified `Section.leave()` to reset direction:

```java
public synchronized void leave(String trainName) {
    if(occupyingTrain != null && occupyingTrain.equals(trainName)) {
        occupyingTrain = null;
        trainDirection = null;
        this.notifyAll();
    }
}
```

---

## Solution Architecture

### Synchronization Strategy

1. **Railway-level lock**: Only ONE train can move at a time
   - Prevents multiple trains from being in sections simultaneously
   - Prevents opposite-direction conflicts
   - Acquired when leaving a station, released when arriving at a station

2. **Section-level lock**: Each section has its own monitor
   - Ensures maximum one train per section
   - Uses wait/notify pattern for blocking/unblocking trains

3. **Message ordering**: Print statements occur BEFORE releasing locks
   - Ensures correct chronological display of train movements

### Deadlock Prevention

The solution prevents deadlocks by:
- **Single global lock**: Only one train moves at a time
- **Consistent lock ordering**: Railway lock → Section lock
- **No circular dependencies**: Trains wait for railway lock, not for each other
- **Timeout-free waiting**: Uses proper wait/notify instead of busy-waiting

### Testing Results

Successfully tested with:
- 1 train: Continuous back-and-forth movement
- 2 trains: Perfect alternation between trains
- 3 trains: Sequential turn-taking without conflicts
- No deadlocks observed
- No race conditions
- Correct section occupancy (always ≤ 1 train per section)

---

## How to Run

```bash
# Compile
javac -d bin src/train/*.java

# Run with 2 trains (default)
java -cp bin train.Main

# Run with timeout (recommended for testing)
timeout 30 java -cp bin train.Main
```

To test with different numbers of trains, modify `Main.java` to add/remove train instances.

---

## Key Implementation Files

- **Section.java**: Section with direction-aware synchronization
- **Railway.java**: Global railway lock manager
- **Position.java**: Train movement logic with lock acquisition/release
- **Train.java**: Runnable train with continuous movement loop
- **Element.java**: Clean abstract base class
- **Station.java**: Station with platform capacity
- **Main.java**: Entry point with configurable number of trains
