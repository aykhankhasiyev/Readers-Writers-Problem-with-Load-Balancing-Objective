# Readers-Writers Problem with Load Balancing

## Introduction

The readers-writers problem is a classic synchronization problem in operating systems. It involves coordinating access to shared resources between reader and writer threads.

In this project, the problem is extended by introducing:

- writer priority
- load balancing between file replicas

## System Design

The system consists of three main components:

1. Reader threads
2. Writer thread
3. FileManager synchronization controller

### Reader Threads

Readers are created at random intervals. Each reader:

- selects the least-used file replica
- reads the file once
- logs the operation
- terminates

### Writer Thread

A single writer thread:

- sleeps for a random period
- locks all replicas
- updates the file content
- logs the operation

## Synchronization

Synchronization is implemented using:

- ReentrantLock
- Condition variables

The lock ensures mutual exclusion during critical sections.

### Writer Priority

Readers must wait when:

- a writer is active
- a writer is waiting

Condition:

while (writerActive || waitingWriters > 0)

This prevents writer starvation.

### Load Balancing

Readers select the replica with the lowest number of active readers.

This ensures balanced usage of replicas.

## Logging

All operations are recorded in a log file. Each entry includes:

- reader ID
- file replica accessed
- number of readers per replica
- writer status
- file content

## Conclusion

The implemented solution successfully satisfies all assignment requirements:

- thread safety
- writer priority
- load balancing
- synchronized file updates