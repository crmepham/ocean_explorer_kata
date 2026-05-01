package org.kata.domain;

import org.junit.jupiter.api.Test;
import org.kata.command.MoveForwardCommand;
import org.kata.command.TurnRightCommand;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProbeTest {

    @Test
    void initialStateHasCorrectPositionDirectionAndSingleHistoryEntry() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);

        assertThat(probe.position()).isEqualTo(new Position2D(0, 0));
        assertThat(probe.direction()).isEqualTo(Direction.NORTH);
        assertThat(probe.visitHistory()).hasSize(1);
        assertThat(probe.visitHistory().get(0)).isEqualTo(new Position2D(0, 0));
    }

    @Test
    void movingToNewPositionUpdatesPositionAndAddsToVisitHistory() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);

        probe.moveTo(new Position2D(0, 1));

        assertThat(probe.position()).isEqualTo(new Position2D(0, 1));
        assertThat(probe.visitHistory()).hasSize(2);
        assertThat(probe.visitHistory().get(1)).isEqualTo(new Position2D(0, 1));
    }

    @Test
    void turningLeftChangesDirectionCounterclockwise() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);

        probe.turnLeft();

        assertThat(probe.direction()).isEqualTo(Direction.NORTHWEST);
    }

    @Test
    void turningRightChangesDirectionClockwise() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);

        probe.turnRight();

        assertThat(probe.direction()).isEqualTo(Direction.NORTHEAST);
    }

    @Test
    void executingFfrrffOnEmptyGridEndsAtCorrectPositionFacingEast() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);
        OceanFloor grid = new OceanFloor(10, 10, Set.of());

        ExecutionSummary summary = probe.execute(
                List.of(
                        new MoveForwardCommand(),
                        new MoveForwardCommand(),
                        new TurnRightCommand(),
                        new TurnRightCommand(),
                        new MoveForwardCommand(),
                        new MoveForwardCommand()
                ),
                grid
        );

        assertThat(probe.position()).isEqualTo(new Position2D(2, 2));
        assertThat(probe.direction()).isEqualTo(Direction.EAST);
        assertThat(probe.visitHistory()).hasSize(5);
        assertThat(summary.commandsExecuted()).isEqualTo(6);
        assertThat(summary.halted()).isFalse();
    }

    @Test
    void executingCommandsHaltedByObstacleReturnsCorrectExecutedCount() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);
        OceanFloor grid = new OceanFloor(10, 10, Set.of(new Position2D(0, 2)));

        ExecutionSummary summary = probe.execute(
                List.of(
                        new MoveForwardCommand(),
                        new MoveForwardCommand(),
                        new MoveForwardCommand()
                ),
                grid
        );

        assertThat(probe.position()).isEqualTo(new Position2D(0, 1));
        assertThat(summary.commandsExecuted()).isEqualTo(1);
        assertThat(summary.halted()).isTrue();
    }

    @Test
    void executingCommandsHaltedByOutOfBoundsReturnsCorrectExecutedCount() {
        Probe probe = new Probe(new Position2D(0, 0), Direction.NORTH);
        OceanFloor grid = new OceanFloor(1, 1, Set.of());

        ExecutionSummary summary = probe.execute(
                List.of(
                        new MoveForwardCommand()
                ),
                grid
        );

        assertThat(probe.position()).isEqualTo(new Position2D(0, 0));
        assertThat(summary.commandsExecuted()).isEqualTo(0);
        assertThat(summary.halted()).isTrue();
    }
}
