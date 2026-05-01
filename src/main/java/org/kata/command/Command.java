package org.kata.command;

import org.kata.domain.Grid;
import org.kata.domain.MoveResult;
import org.kata.domain.Probe;

public interface Command {
    MoveResult execute(Probe probe, Grid grid);
}
