package org.kata.parser;

import org.junit.jupiter.api.Test;
import org.kata.command.MoveBackwardCommand;
import org.kata.command.MoveForwardCommand;
import org.kata.command.TurnLeftCommand;
import org.kata.command.TurnRightCommand;
import org.kata.exception.InvalidCommandException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandParserTest {

    @Test
    void parsesForwardCommandCharacter() {
        assertThat(CommandParser.parse("F")).hasSize(1)
                .first().isInstanceOf(MoveForwardCommand.class);
    }

    @Test
    void parsesBackwardCommandCharacter() {
        assertThat(CommandParser.parse("B")).hasSize(1)
                .first().isInstanceOf(MoveBackwardCommand.class);
    }

    @Test
    void parsesTurnLeftCommandCharacter() {
        assertThat(CommandParser.parse("L")).hasSize(1)
                .first().isInstanceOf(TurnLeftCommand.class);
    }

    @Test
    void parsesTurnRightCommandCharacter() {
        assertThat(CommandParser.parse("R")).hasSize(1)
                .first().isInstanceOf(TurnRightCommand.class);
    }

    @Test
    void parsesAllFourCommandCharactersInSequence() {
        List<?> commands = CommandParser.parse("FBLR");

        assertThat(commands).hasSize(4);
        assertThat(commands.get(0)).isInstanceOf(MoveForwardCommand.class);
        assertThat(commands.get(1)).isInstanceOf(MoveBackwardCommand.class);
        assertThat(commands.get(2)).isInstanceOf(TurnLeftCommand.class);
        assertThat(commands.get(3)).isInstanceOf(TurnRightCommand.class);
    }

    @Test
    void throwsInvalidCommandExceptionForUnknownCharacter() {
        assertThatThrownBy(() -> CommandParser.parse("FXB"))
                .isInstanceOf(InvalidCommandException.class);
    }
}
