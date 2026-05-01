package org.kata.command;

import org.kata.domain.Grid;
import org.kata.domain.MoveResult;
import org.kata.domain.Probe;

public class TurnLeftCommand implements Command {

    @Override
    public MoveResult execute(Probe probe, Grid grid) {
        probe.turnLeft();
        return new MoveResult.Success();
    }
}
