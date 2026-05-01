package org.kata.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DirectionTest {

    @ParameterizedTest(name = "{0} turnLeft is {1}")
    @CsvSource({
            "NORTH, NORTHWEST",
            "NORTHWEST, WEST",
            "WEST, SOUTHWEST",
            "SOUTHWEST, SOUTH",
            "SOUTH, SOUTHEAST",
            "SOUTHEAST, EAST",
            "EAST, NORTHEAST",
            "NORTHEAST, NORTH"
    })
    void turningLeftRotatesCounterclockwiseOneStep(Direction from, Direction expected) {
        assertThat(from.turnLeft()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} turnRight is {1}")
    @CsvSource({
            "NORTH, NORTHEAST",
            "NORTHEAST, EAST",
            "EAST, SOUTHEAST",
            "SOUTHEAST, SOUTH",
            "SOUTH, SOUTHWEST",
            "SOUTHWEST, WEST",
            "WEST, NORTHWEST",
            "NORTHWEST, NORTH"
    })
    void turningRightRotatesClockwiseOneStep(Direction from, Direction expected) {
        assertThat(from.turnRight()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} forwardVector is ({1},{2})")
    @CsvSource({
            "NORTH,      0,  1",
            "NORTHEAST,  1,  1",
            "EAST,       1,  0",
            "SOUTHEAST,  1, -1",
            "SOUTH,      0, -1",
            "SOUTHWEST, -1, -1",
            "WEST,      -1,  0",
            "NORTHWEST, -1,  1"
    })
    void forwardVectorReturnsCorrectMovementVector(Direction direction, int expectedX, int expectedY) {
        MovementVector vector = direction.forwardVector();
        assertThat(vector.directionX()).isEqualTo(expectedX);
        assertThat(vector.directionY()).isEqualTo(expectedY);
    }

    @ParameterizedTest(name = "{0} backwardVector is ({1},{2})")
    @CsvSource({
            "NORTH,      0, -1",
            "NORTHEAST, -1, -1",
            "EAST,      -1,  0",
            "SOUTHEAST, -1,  1",
            "SOUTH,      0,  1",
            "SOUTHWEST,  1,  1",
            "WEST,       1,  0",
            "NORTHWEST,  1, -1"
    })
    void backwardVectorReturnsOppositeOfForwardVector(Direction direction, int expectedX, int expectedY) {
        MovementVector vector = direction.backwardVector();
        assertThat(vector.directionX()).isEqualTo(expectedX);
        assertThat(vector.directionY()).isEqualTo(expectedY);
    }
}
