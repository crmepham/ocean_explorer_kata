package org.kata.service.command;

import org.kata.domain.Direction;
import org.kata.service.probe.PositionResponse;

public record CommandResponse(
        Long probeId,
        CommandResult result,
        int commandsSubmitted,
        int commandsExecuted,
        PositionResponse finalPosition,
        Direction finalDirection
) {}
