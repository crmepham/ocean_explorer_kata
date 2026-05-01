# Readme

## How to review this project

I have paired with Claude code to design and implement this coding task. The requirements are captured in `Requirements.md`. You may want to
start with `Design.md` and `API.md` which together drive the design of the implementation. Please review `ClaudeLog.md` to understand how I guided
Claude, used TDD and clean code principles, and generally steered the AI along.

## How to run the application

If you want to test the application running on your local machine:

1. Create a database in a locally running instance of MySQL 8
2. Create a `.env` file with the following contents:
```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ocean_explorer_kata
DB_USERNAME=<username>
DB_PASSWORD=<password>
```
3. In IntelliJ run `OceanExplorerApplication` referencing the `.env` file. Or use in the terminal `mvn spring-boot:run` after exporting the environment variables.
```
export DB_HOST=localhost
export DB_PORT=3306
...
```

## Using the API

Import the `OceanExplorerKata.postman_collection` into Postman. Start by creating a grid!
