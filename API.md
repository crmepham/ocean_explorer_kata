# Ocean Explorer — REST API Design

## Status: DRAFT — Under Discussion

---

## 1. Principles

- Resources are **nouns**, not verbs
- Use standard **HTTP methods** to express intent (POST to create, GET to read)
- Use standard **HTTP status codes** to express outcome
- Return a `Location` header on `201 Created` responses
- Errors return a consistent error body
- The API is **stateless** — each request contains all information needed to process it

---

## 2. Resources

| Resource | Description |
|---|---|
| `Grid` | The ocean floor — defines dimensions and obstacle positions |
| `Probe` | The submersible — deployed on a grid with an initial position and direction |
| `Commands` | A sequence of instructions submitted to a probe |
| `Visits` | The ordered history of positions a probe has occupied |

---

## 3. Endpoints

### 3.1 Grids

#### Create a grid
```
POST /grids
```

**Request body:**
```json
{
  "width": 10,
  "height": 10,
  "obstacles": [
    { "x": 3, "y": 4 },
    { "x": 7, "y": 2 }
  ]
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "width": 10,
  "height": 10,
  "obstacles": [
    { "x": 3, "y": 4 },
    { "x": 7, "y": 2 }
  ]
}
```
`Location: /grids/1`

---

#### Get a grid
```
GET /grids/{gridId}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "width": 10,
  "height": 10,
  "obstacles": [
    { "x": 3, "y": 4 },
    { "x": 7, "y": 2 }
  ]
}
```

---

#### List all grids
```
GET /grids
```

**Response `200 OK`:**
```json
[
  { "id": 1, "width": 10, "height": 10, "obstacles": [] },
  { "id": 2, "width": 5,  "height": 8,  "obstacles": [] }
]
```

---

#### Update a grid
```
PUT /grids/{gridId}
```

**Request body:**
```json
{
  "width": 20,
  "height": 15,
  "obstacles": []
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "width": 20,
  "height": 15,
  "obstacles": []
}
```

---

#### Delete a grid
```
DELETE /grids/{gridId}
```

**Response `204 No Content`**

---

### 3.2 Obstacles

#### Place an obstacle on a grid
```
POST /grids/{gridId}/obstacles
```

Placing an obstacle at a position that already contains an obstacle is idempotent — returns `201` with the existing obstacle.

**Request body:**
```json
{
  "x": 3,
  "y": 4
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "gridId": 1,
  "x": 3,
  "y": 4
}
```
`Location: /grids/1/obstacles/1`

| Scenario | Status |
|---|---|
| Position is empty or already has an obstacle | `201 Created` |
| A probe occupies the position | `400 Bad Request` |
| Grid does not exist | `404 Not Found` |
| Position is outside grid bounds | `422 Unprocessable Entity` |

---

### 3.3 Probes

#### Deploy a probe onto a grid
```
POST /grids/{gridId}/probes
```

**Request body:**
```json
{
  "x": 0,
  "y": 0,
  "direction": "NORTH"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "gridId": 1,
  "position": { "x": 0, "y": 0 },
  "direction": "NORTH"
}
```
`Location: /probes/1`

---

#### Get probe state
```
GET /probes/{probeId}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "gridId": 1,
  "position": { "x": 1, "y": 2 },
  "direction": "EAST"
}
```

---

### 3.3 Commands

#### Submit a command sequence
```
POST /probes/{probeId}/commands
```

Commands are submitted as a string of characters:

| Character | Command |
|---|---|
| `F` | Move forward |
| `B` | Move backward |
| `L` | Turn left |
| `R` | Turn right |

**Request body:**
```json
{
  "commands": "FFLRF"
}
```

**Response `200 OK` — sequence completed:**
```json
{
  "probeId": 1,
  "result": "SUCCESS",
  "commandsSubmitted": 5,
  "commandsExecuted": 5,
  "finalPosition": { "x": 1, "y": 2 },
  "finalDirection": "EAST"
}
```

**Response `200 OK` — sequence halted by obstacle:**
```json
{
  "probeId": 1,
  "result": "BLOCKED_BY_OBSTACLE",
  "commandsSubmitted": 5,
  "commandsExecuted": 2,
  "finalPosition": { "x": 0, "y": 1 },
  "finalDirection": "NORTH"
}
```

> Note: A blocked sequence is not an error — the probe still moved to its halting position. Hence `200 OK`, not `4xx`. The `result` field distinguishes outcomes.

---

### 3.4 Visits

#### Get visit history
```
GET /probes/{probeId}/visits
```

Returns the ordered list of all positions the probe has occupied, including the initial deployment position.

**Response `200 OK`:**
```json
{
  "probeId": 1,
  "visits": [
    { "x": 0, "y": 0 },
    { "x": 0, "y": 1 },
    { "x": 0, "y": 2 },
    { "x": 1, "y": 2 }
  ]
}
```

---

## 4. HTTP Status Codes

| Code | When |
|---|---|
| `200 OK` | Successful GET or command execution (including blocked outcomes) |
| `201 Created` | Grid or probe successfully created |
| `400 Bad Request` | Malformed request body, unknown command character, or obstacle placed at a probe's position |
| `404 Not Found` | Grid or probe ID does not exist |
| `409 Conflict` | Probe deployment position is occupied by an obstacle |
| `422 Unprocessable Entity` | Position is outside the grid bounds |

---

## 5. Error Response Body

All error responses return a consistent body:

```json
{
  "status": 400,
  "error": "INVALID_COMMAND",
  "message": "Unknown command character 'X' at position 3"
}
```

---

## 6. Decisions

| # | Question | Decision |
|---|---|---|
| 1 | **Framework** | Spring Boot (latest stable) with Spring Web and Spring Data JPA |
| 2 | **Persistence** | MySQL 8 standalone database. Flyway for schema migrations. |
| 3 | **Probe IDs** | Auto-incremented integers generated by the database (`AUTO_INCREMENT`) |
