package org.kata.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Position2DTest {

    @Test
    void storesXAndYCoordinates() {
        Position2D position = new Position2D(3, 7);

        assertThat(position.x()).isEqualTo(3);
        assertThat(position.y()).isEqualTo(7);
    }

    @Test
    void translatingByVectorReturnsNewPositionWithAddedDeltas() {
        Position2D position = new Position2D(2, 3);
        MovementVector vector = new MovementVector(1, -1);

        Position result = position.translate(vector);

        assertThat(result).isEqualTo(new Position2D(3, 2));
    }

    @Test
    void twoPositionsWithSameCoordinatesAreEqual() {
        assertThat(new Position2D(4, 5)).isEqualTo(new Position2D(4, 5));
    }

    @Test
    void twoPositionsWithDifferentCoordinatesAreNotEqual() {
        assertThat(new Position2D(4, 5)).isNotEqualTo(new Position2D(4, 6));
    }
}
