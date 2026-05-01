package org.kata.parser;

import org.kata.command.Command;
import org.kata.command.MoveBackwardCommand;
import org.kata.command.MoveForwardCommand;
import org.kata.command.TurnLeftCommand;
import org.kata.command.TurnRightCommand;
import org.kata.exception.InvalidCommandException;

import java.util.List;
import java.util.stream.IntStream;

public final class CommandParser {

    private CommandParser() {}

    public static List<Command> parse(String commands) {
        return IntStream.range(0, commands.length())
                .mapToObj(i -> toCommand(commands.charAt(i), i))
                .toList();
    }

    private static Command toCommand(char character, int position) {
        return switch (character) {
            case 'F' -> new MoveForwardCommand();
            case 'B' -> new MoveBackwardCommand();
            case 'L' -> new TurnLeftCommand();
            case 'R' -> new TurnRightCommand();
            default -> throw new InvalidCommandException(character, position);
        };
    }
}
