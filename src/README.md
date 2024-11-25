# **Election System using Paxos Algorithm**

## **Overview**
This project implements a simulation of the Paxos consensus algorithm where a set of council members vote on a candidate for president. The election process is driven by proposals, and the system ensures that only one candidate can be elected, even if multiple proposals are made simultaneously.

## **Files**

1. **ElectionCoordinator.java**  
   The entry point of the system that initialises and runs the election, managing the election process and council members.

2. **CouncilMember.java**  
   Represents each council member in the election, handling communication, proposal, and vote counting.

3. **Message.java**  
   A helper class for defining messages that are exchanged between council members.

4. **ElectionCoordinatorTest.java**  
   Tests for verifying the election process, including tests for simultaneous proposals and immediate responses.

---

## **Prerequisites**
- **Java 17 or higher**: The project uses Java for object serialisation and multi-threading.
- **JUnit 5**: The testing framework for running unit tests.

Make sure you have **Java 17+** and **JUnit 5** properly configured.

---

## **Running the Election**

### **1. Compile the Code**
To compile all Java files, run:
```bash
javac *.java
```

### **2. Run the Election**
To start the election, run the `ElectionCoordinator` class:
```bash
java ElectionCoordinator
```
This will initialise the 9 council members and start the election process, where a random member proposes a candidate, and the system proceeds with the Paxos protocol.

### **3. Simulating Proposals**
You can modify the logic to simulate simultaneous proposals by adjusting the code in `ElectionCoordinator.java`. The current implementation uses randomisation to select the proposer and candidate for each election. Just adjust the commented out section to switch to testing simultaneous proposals.

### **4. Test the Election**

If running the JUnit tests, run `ElectionCoordinatorTest.java` instead of `ElectionCoordinator.java`. If you're using an IDE like IntelliJ IDEA or Eclipse, you can right-click `ElectionCoordinatorTest.java` and run the tests directly.

---

## **Testing the Election Process**

The tests ensure the proper functionality of the election system under different conditions:

1. **Simultaneous Proposals** (`testSimultaneousProposals`):
    - Simulates multiple council members proposing a candidate at the same time.
    - Validates that only one candidate can be elected president.

2. **Immediate Responses** (`testImmediateResponses`):
    - Simulates an election where all council members immediately respond to voting queries.
    - Ensures that a candidate can be elected under optimal conditions.

The test results will indicate if the system behaves as expected.

---
