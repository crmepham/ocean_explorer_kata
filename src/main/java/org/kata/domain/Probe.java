package org.kata.domain;

import org.kata.command.Command;
import org.kata.service.command.CommandResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Probe {

    private Position2D position;
    private Direction direction;
    private final List<Position2D> visitHistory;

    public Probe(Position2D position, Direction direction) {
        this.position = position;
        this.direction = direction;
        this.visitHistory = new ArrayList<>();
        this.visitHistory.add(position);
    }

    public void moveTo(Position2D newPosition) {
        this.position = newPosition;
        this.visitHistory.add(newPosition);
    }

    public void turnLeft() {
        this.direction = direction.turnLeft();
    }

    public void turnRight() {
        this.direction = direction.turnRight();
    }

    public Position2D position() {
        return position;
    }

    public Direction direction() {
        return direction;
    }

    public List<Position2D> visitHistory() {
        return Collections.unmodifiableList(visitHistory);
    }

    public ExecutionSummary execute(List<Command> commands, Grid grid) {
        int executed = 0;
        for (Command command : commands) {
            MoveResult result = command.execute(this, grid);
            if (result instanceof MoveResult.ObstacleBlocked) {
                return new ExecutionSummary(executed, true, CommandResult.BLOCKED_BY_OBSTACLE);
            }
            if (result instanceof MoveResult.BoundaryBlocked) {
                return new ExecutionSummary(executed, true, CommandResult.BLOCKED_BY_BOUNDARY);
            }
            executed++;
        }
        return new ExecutionSummary(executed, false, CommandResult.SUCCESS);
    }
}
