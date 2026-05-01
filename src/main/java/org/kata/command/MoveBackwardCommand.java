package org.kata.command;

import org.kata.domain.Grid;
import org.kata.domain.MoveResult;
import org.kata.domain.Position;
import org.kata.domain.Position2D;
import org.kata.domain.Probe;

public class MoveBackwardCommand implements Command {

    @Override
    public MoveResult execute(Probe probe, Grid grid) {
        Position newPosition = probe.position().translate(probe.direction().backwardVector());
        if (!grid.isWithinBounds(newPosition)) {
            return new MoveResult.BoundaryBlocked();
        }
        if (grid.isObstacle(newPosition)) {
            return new MoveResult.ObstacleBlocked();
        }
        probe.moveTo((Position2D) newPosition);
        return new MoveResult.Success();
    }
}
