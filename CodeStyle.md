# Code Style Guide

Coding conventions to follow throughout this project.

---

## Naming

- Use full, descriptive names — avoid abbreviations.
  - `directionX` / `directionY` not `dx` / `dy`
  - `movementVector` not `mv`
  - `boundaryPolicy` not `bp`

---

## Tests

- Test method names should be written as plain English phrases describing the behaviour, not the implementation.
  - `storesDeltaValues` not `testMovementVector`

---

---

## API Tests

- Build request payloads using objects serialised with `ObjectMapper`, not raw JSON strings.
  - `objectMapper.writeValueAsString(new GridRequest(10, 10, List.of()))` not `"{ \"width\": 10, ... }"`
  - If the request structure changes, only the object needs updating — not a fragile string

---

## More to be added as we go.
