package org.kata.domain;

import org.kata.service.command.CommandResult;

public record ExecutionSummary(int commandsExecuted, boolean halted, CommandResult commandResult) {}
