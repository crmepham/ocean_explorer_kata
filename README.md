# Readme

## How to review this project

I have paired with Claude code to design and implement this coding task. The requirements are captured in `Requirements.md`. You may want to
start with `Design.md` and `API.md` which together drive the design of the implementation. Please review `ClaudeLog.md` to understand how I guided
Claude, used TDD and clean code principles, and generally steered the AI along.

## How to run the application

If you want to test the application running on your local machine:

1. Checkout this repository
2. Build the jar: `mvn clean install -DskipTests`
3. Start Postgresql from the root directory: `docker-compose up`
4. Start the app: `DB_HOST=localhost DB_PORT=5432 DB_NAME=ocean_explorer_kata DB_USERNAME=ocean DB_PASSWORD=ocean mvn spring-boot:run`

## Using the API

Import the `OceanExplorerKata.postman_collection` into Postman. Start by creating a grid!

## Webpage

Navigate to `http://localhost:8080` to see a visual representation of the grids.
