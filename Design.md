# Ocean Explorer Kata — Design Document

## Status: DRAFT — Under Discussion

---

## 1. Overview

A Java API that controls a remotely operated submersible probe navigating a grid-based ocean floor. The probe accepts a sequence of commands, moves within defined boundaries, avoids obstacles, and reports its visit history.

The design is intentionally extensible to support future requirements such as a three-dimensional grid and additional movement directions (e.g. diagonal headings like `NORTHWEST`).

---

## 2. Implementation Principles

- **SOLID** — each class has a single responsibility; dependencies flow toward abstractions
- **DRY** — no duplicated logic; shared behaviour extracted into cohesive units
- **TDD** — every behaviour is driven by a failing test first

---

## 3. Domain Model

### Abstractions (interfaces)

| Type | Responsibility |
|---|---|
| `Position` | Interface representing a location in the grid. Implementations define dimensionality. Immutable contract: `translate(MovementVector)` returns a new `Position`. |
| `Direction` | Interface representing a heading. Defines `turnLeft()`, `turnRight()`, `forwardVector()`, and `backwardVector()`. Decoupled from Position dimensionality. |
| `Grid` | Interface. Answers: is a position within bounds? Is a position blocked by an obstacle? |
| `Command` | Interface (GoF Command Pattern). Each command knows how to `execute(Probe)`. New commands are added as new classes — existing code is untouched. |

### Value Objects (immutable implementations)

| Type | Responsibility |
|---|---|
| `Position2D(x, y)` | Concrete `Position` for a 2D grid. Implements `equals`/`hashCode` for use in collections (visit tracking). |
| `Position3D(x, y, z)` | Future concrete `Position` for a 3D grid — no changes to `Probe` or `Grid` needed. Kept as a separate class because a 2D and 3D position are genuinely different: consolidating would lose named axis semantics and compile-time dimension safety. |
| `MovementVector` | Encodes a directional delta (e.g. `dx, dy`). Produced by `Direction`; consumed by `Position.translate()`. Dimensionality-aware in its concrete form. |
| `Direction` | Single enum implementing the `Direction` interface. Contains all 8 compass points: `NORTH`, `NORTHEAST`, `EAST`, `SOUTHEAST`, `SOUTH`, `SOUTHWEST`, `WEST`, `NORTHWEST`. Each entry defines its own `turnLeft()`, `turnRight()`, and `MovementVector`. Diagonal entries are unused until needed — no changes required when support is added. |

### Concrete Commands (implementing `Command`)

| Type | Behaviour |
|---|---|
| `MoveForwardCommand` | Moves the probe one step in its current heading direction. |
| `MoveBackwardCommand` | Moves the probe one step opposite to its current heading. |
| `TurnLeftCommand` | Rotates the probe's heading left (delegates to `Direction.turnLeft()`). |
| `TurnRightCommand` | Rotates the probe's heading right (delegates to `Direction.turnRight()`). |

> Future commands (e.g. `DiveCommand`, `AscendCommand` for 3D) are added as new classes. `Probe` and existing commands are unchanged — OCP in practice.

### Core Domain

| Type | Responsibility |
|---|---|
| `OceanFloor` | Concrete `Grid` implementation. Holds grid dimensions and a set of obstacle positions. |
| `Probe` | Main entity. Holds current `Position` and `Direction`. Executes `Command` objects against the `Grid`. Tracks visit history as an ordered list of `Position`s. |
| `MoveResult` | Outcome of a move attempt: `SUCCESS` (new position) or `BLOCKED` (boundary or obstacle hit). Avoids exceptions as control flow. |

### Supporting

| Type | Responsibility |
|---|---|
| `CommandParser` | Converts a string of command characters (e.g. `"FFLRF"`) into a `List<Command>`. Isolated so parsing strategy can vary independently. |
| `BoundaryPolicy` | Interface (Strategy Pattern). Called by `Probe` when a move would leave the grid. Returns the position to use. Swapping the implementation changes boundary behaviour without touching `Probe`. |
| `BlockingBoundaryPolicy` | Concrete `BoundaryPolicy`. Returns the probe's current position — it stays put at the edge. |
| `WrappingBoundaryPolicy` | Future concrete `BoundaryPolicy`. Wraps the probe to the opposite edge of the grid. |
| `ObstaclePolicy` | Interface (Strategy Pattern). Called by `Probe` when a move is blocked by an obstacle. Decides whether to halt the command sequence or continue. Swapping the implementation changes collision behaviour without touching `Probe`. |
| `HaltingObstaclePolicy` | Concrete `ObstaclePolicy`. Stops the command sequence immediately on the first obstacle hit. |
| `SkippingObstaclePolicy` | Future concrete `ObstaclePolicy`. Skips the blocked move and continues executing remaining commands. |

---

## 4. Key Extensibility Points

| Future Requirement | How the Design Accommodates It |
|---|---|
| 3D grid | Add `Position3D` implementing `Position`; add `MovementVector3D`. `Grid`, `Probe`, `Command` are unchanged. |
| Diagonal directions (e.g. `NORTHWEST`) | Already present in the `Direction` enum — rotation rules and movement vectors are pre-defined. Simply use them. |
| New commands (e.g. `DIVE`, `ASCEND`) | Add a new `Command` implementation class. No changes to `Probe` or existing commands. |
| Alternative grid shapes | Add a new `Grid` implementation. `Probe` is unaware of the concrete grid type. |
| Alternative boundary behaviour (e.g. wrapping) | Provide a new `BoundaryPolicy` implementation. `Probe` is injected with the policy and is unaware of the concrete strategy. |
| Alternative obstacle behaviour (e.g. skip and continue) | Provide a new `ObstaclePolicy` implementation. `Probe` is injected with the policy and is unaware of the concrete strategy. |

---

## 5. SOLID Application

| Principle | How it is applied |
|---|---|
| **Single Responsibility** | `Grid` manages spatial constraints. `Probe` manages state and history. `CommandParser` handles input parsing. `Direction` handles rotation logic. |
| **Open/Closed** | New commands are new classes, not modifications. New directions are new implementations. New dimensions are new `Position`/`MovementVector` implementations. |
| **Liskov Substitution** | Any `Grid` implementation is substitutable. Any `Direction` implementation is substitutable in `Probe`. Any `Position` implementation works with `translate()`. |
| **Interface Segregation** | `Grid` exposes only `isWithinBounds(Position)` and `isObstacle(Position)`. `Direction` exposes only what movement and rotation require. |
| **Dependency Inversion** | `Probe` depends on `Grid`, `Direction`, `Position`, and `Command` interfaces — never on concrete implementations. |

---

## 6. Testing Strategy

### Philosophy
Every behaviour is driven by a failing test first (Red → Green → Refactor). No production code is written without a test requiring it.

### Test Layers

| Layer | Tool | Scope | Speed |
|---|---|---|---|
| **Domain unit tests** | JUnit 5 + AssertJ | A single class in isolation; no Spring context, no database | Very fast |
| **Controller tests** | `@WebMvcTest` + Mockito | Web layer only; service dependencies mocked with `@MockitoBean` | Fast |
| **Integration tests** | `@SpringBootTest` | Full application stack including database | Slow — run separately |

### Layer Rationale

**Domain unit tests** cover the core logic independently of the framework. `MovementVector`, `Direction`, `Position2D`, `OceanFloor`, commands, and `Probe` are all plain Java — they require no Spring context and should never need one.

**Controller tests** use `@WebMvcTest`, which loads only the web layer (controllers, filters, serialisation). The service layer is replaced with a `@MockitoBean` stub returning known values. This isolates the test to the controller's responsibilities: routing, HTTP status codes, request deserialisation, response serialisation, and headers. The service's correctness is covered separately by its own unit tests.

**Integration tests** exercise the full stack end-to-end against a real database. These validate that all layers compose correctly and that Flyway migrations are consistent with JPA mappings.

### JUnit 5 Features in Use

| Feature | When used |
|---|---|
| `@Test` | All standard test cases |
| `@Nested` | Grouping related scenarios within a test class (e.g. forward movement: happy path, boundary, obstacle) |
| `@ParameterizedTest` | Testing multiple input variations without duplicating test methods (e.g. all 8 rotation steps of `Direction`) |
| `@WebMvcTest` | Controller-layer tests |
| `@SpringBootTest` | Integration tests |
| `@MockitoBean` | Replacing Spring beans with Mockito stubs in `@WebMvcTest` |

### AssertJ
Fluent assertions are used throughout. Prefer `assertThat(x).isEqualTo(y)` over JUnit's `assertEquals(y, x)` — it produces clearer failure messages and reads naturally.

---

## 7. TDD Build Order

Tests will be written in this order, each driving the minimum implementation needed:

**Domain:**
1. `MovementVector` — stores direction values ✅
2. `Direction` — left/right rotation cycle, forward/backward vectors
3. `Position2D` — equality, hashCode, translate with a vector
4. `OceanFloor` — boundary validation, obstacle detection
5. `MoveForwardCommand` / `MoveBackwardCommand` — happy path movement, boundary block, obstacle block
6. `TurnLeftCommand` / `TurnRightCommand` — heading changes
7. `Probe` — initial state, full command execution sequence, visit history
8. `CommandParser` — valid input, unknown character handling

**API:**

9. `GridController` — `POST /grids` returns 201 with Location header and grid details
10. `ProbeController` — `POST /grids/{gridId}/probes`, `GET /probes/{probeId}`
11. `CommandController` — `POST /probes/{probeId}/commands`
12. `VisitController` — `GET /probes/{probeId}/visits`

**Integration:**

13. Full command sequence producing expected final state and visit summary

---

## 7. Open Questions

The following decisions are pending and will update this document once agreed:

| # | Question | Options | Decision |
|---|---|---|---|
| 1 | **Grid boundary behaviour** | (a) Probe is blocked at edge, (b) Probe wraps to opposite side | **Blocked at edge** via `BlockingBoundaryPolicy`. Behaviour is swappable via `BoundaryPolicy` interface (Strategy Pattern) without changing `Probe`. |
| 2 | **Obstacle collision behaviour** | (a) Blocked move is skipped, sequence continues, (b) Sequence halts on first block | **Halt on first block** via `HaltingObstaclePolicy`. Behaviour is swappable via `ObstaclePolicy` interface (Strategy Pattern) without changing `Probe`. |
| 3 | **JUnit version** | Upgrade from 3.8.1 to JUnit 5 + AssertJ? | **JUnit 5 (Jupiter) + AssertJ**. Enables `@Test`, `@Nested`, `@ParameterizedTest`, and fluent assertions. |
| 4 | **Java version** | Java 17+ enables records (clean value objects) and sealed classes (clean `MoveResult`) | **Java 26**. `Position2D`, `MovementVector` implemented as `record`s. `MoveResult` as a `sealed interface` with `record` implementations. Pattern matching used in switch expressions. |

---

## 8. Package Structure (proposed)

```
org.kata
├── command
│   ├── Command.java                    (interface)
│   ├── MoveBackwardCommand.java
│   ├── MoveForwardCommand.java
│   ├── TurnLeftCommand.java
│   └── TurnRightCommand.java
├── domain
│   ├── Direction.java                  (enum — all 8 compass points)
│   ├── Grid.java                       (interface)
│   ├── MovementVector.java
│   ├── MoveResult.java
│   ├── OceanFloor.java                 (implements Grid)
│   ├── Position.java                   (interface)
│   ├── Position2D.java                 (implements Position)
│   └── Probe.java
├── policy
│   ├── BoundaryPolicy.java             (interface)
│   ├── BlockingBoundaryPolicy.java
│   ├── WrappingBoundaryPolicy.java     (future)
│   ├── ObstaclePolicy.java             (interface)
│   ├── HaltingObstaclePolicy.java
│   └── SkippingObstaclePolicy.java     (future)
└── parser
    └── CommandParser.java
```

---

## 9. Key Design Decisions Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-04-30 | `Grid` as interface | Enables `Probe` to be tested in isolation; supports future alternative grid shapes |
| 2026-04-30 | `Direction` as a single 8-point compass enum | All compass points consolidated; diagonals unused until needed; no duplication across two enums. Interface still provides substitutability. |
| 2026-04-30 | `Position` as interface | Separates coordinate concept from dimensionality; 3D support requires only a new implementation |
| 2026-04-30 | `Command` as interface (GoF Command Pattern) | Each command encapsulates its own behaviour; new commands added as new classes, no changes to `Probe` |
| 2026-04-30 | `MovementVector` as explicit value object | Decouples `Direction` from `Position` internals; both can evolve independently |
| 2026-04-30 | `MoveResult` for outcome signalling | Type-safe outcome avoids exceptions as control flow for expected obstacle/boundary hits |
| 2026-04-30 | `BoundaryPolicy` interface (Strategy Pattern) | Boundary behaviour (block vs wrap) is swappable without touching `Probe`; initial implementation is `BlockingBoundaryPolicy` |
| 2026-04-30 | `ObstaclePolicy` interface (Strategy Pattern) | Obstacle collision behaviour (halt vs skip) is swappable without touching `Probe`; initial implementation is `HaltingObstaclePolicy` |
| 2026-04-30 | Java 26 + JUnit 5 (Jupiter) + AssertJ | Records for `Position2D` and `MovementVector`; sealed interface for `MoveResult`; pattern matching in switch; `@ParameterizedTest` and fluent assertions in tests |

## 10. Future considerations
- Caching long-lived data
- Probe position updated in memory, then persisted periodically in the background
- Security
- Logging
- Tracing
- Docker containerization for simpler local deployment
- Limits: entities that can be created, listing/pagination, race conditions (pessimistic or optimistic row locking)
- Event driven processes
- Visualization