# PulseGuard Engine

A concurrent, safety-critical Java engine for real-time infusion monitoring, drug-interaction checking, and clinical alert triage — built on custom data structures, classic design patterns, and multithreaded simulation.

## What this is

PulseGuard models the software layer that would sit behind a clinical infusion pump: it validates proposed doses against safety limits, cross-checks new medications against a patient's active prescriptions and allergies, tracks pump state through a strict lifecycle, and triages every resulting alert by clinical severity — all while multiple pumps are monitored concurrently.

It's built to demonstrate solid fundamentals rather than to be a toy CRUD app: hand-rolled data structures (not just `java.util` wrappers), explicit design patterns, thread-safety, input validation, audit logging, and a real test suite.

## Core components

| Layer | What it does |
|---|---|
| `core/` | Domain model — `Patient`, `Medication`, `Dose`, `InfusionPump`, `ClinicalAlert`, `PumpState` |
| `datastructures/` | Hand-built binary heap (`PriorityAlertQueue`), interaction graph with BFS pathing (`DrugInteractionGraph`), doubly-linked-list LRU cache (`LRUAuditCache`), token-bucket rate limiter |
| `patterns/` | Strategy (dosage calculation), Observer (pump/alert pub-sub), Factory (alert construction) |
| `engine/` | State machine, dosage safety validator, interaction checker, alert triage engine, concurrent monitoring scheduler, JSON-backed interaction graph loader |
| `security/` | Input validation, append-style audit logger |

## Why these data structures, specifically

- **`PriorityAlertQueue`** — a binary heap implemented from scratch (not `java.util.PriorityQueue`) so alert ordering (severity first, then FIFO) is explicit and `insert`/`poll` are demonstrably O(log n).
- **`DrugInteractionGraph`** — an undirected graph where BFS finds not just *direct* drug interactions but the shortest *indirect* interaction path between two drugs.
- **`LRUAuditCache`** — HashMap + manual doubly linked list for O(1) get/put, backing a bounded recent-audit-entry cache.
- **`TokenBucketRateLimiter`** — caps how often a single pump can push alerts, so one misbehaving pump can't flood the triage queue (alarm-fatigue prevention).

## Tech stack

- Java 17, Maven
- Jackson (JSON-backed seed data loading)
- JUnit 5
- GitHub Actions CI (build + test on every push/PR)

## Project structure
```text
pulseguard-engine/
├── src/main/java/com/pulseguard/
│ ├── core/
│ ├── datastructures/
│ ├── patterns/{strategy,observer,factory}/
│ ├── engine/
│ ├── security/
│ └── Main.java
├── src/main/resources/seed-drug-interactions.json
└── src/test/java/com/pulseguard/
├── datastructures/
├── engine/
└── patterns/
```


## Running it

```bash
mvn clean compile   # build
mvn test             # run the test suite
mvn exec:java         # run the demo simulation
mvn verify            # full build + test, same as CI
```

## Demo simulation

`Main.java` walks through four scenarios end-to-end: an interaction check (Aspirin onto a Warfarin patient), a weight-based dose calculation with safety validation, an over-limit dose being flagged, and a pump occlusion alert — then drains the triage queue in priority order with every step written to the audit log.

## Test coverage

22 tests across data structures, engine logic, and dosage strategies — all passing.
```bash
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```


## License

MIT
