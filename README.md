# Readers-Writers Synchronization with Load Balancing

## Description

This project implements the Readers-Writers synchronization problem with additional requirements:

- Writer priority over readers
- Load balancing across file replicas
- Thread-safe synchronization
- Logging of all operations

The system simulates multiple reader threads accessing file replicas while a writer thread periodically updates them.

## Features

- 3 replicas of the same file
- Readers are distributed evenly across replicas
- Writer updates all replicas simultaneously
- Readers cannot access files while the writer is writing
- Logging of all read/write operations

## Project Structure

ReadersWritersAssignment
│
├── src
│   ├── Main.java
│   ├── Reader.java
│   ├── Writer.java
│   └── FileManager.java
│
├── README.md
├── REPORT.md
└── log.txt

## Requirements

Java Development Kit (JDK 8 or newer)

## Compile

Open terminal inside the `src` folder:

javac *.java

## Run

java Main

## Output

Logs are written to:

log.txt

Each log entry includes:

- reader ID
- file replica accessed
- number of readers per replica
- writer status
- file content