package org.kata.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OceanFloorTest {

    private final OceanFloor oceanFloor = new OceanFloor(10, 10, Set.of(new Position2D(3, 3)));

    @Test
    void positionInsideBoundsIsWithinBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(5, 5))).isTrue();
    }

    @Test
    void positionAtOriginIsWithinBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(0, 0))).isTrue();
    }

    @Test
    void positionWithNegativeXIsOutOfBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(-1, 5))).isFalse();
    }

    @Test
    void positionWithNegativeYIsOutOfBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(5, -1))).isFalse();
    }

    @Test
    void positionAtWidthEdgeIsOutOfBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(10, 5))).isFalse();
    }

    @Test
    void positionAtHeightEdgeIsOutOfBounds() {
        assertThat(oceanFloor.isWithinBounds(new Position2D(5, 10))).isFalse();
    }

    @Test
    void positionMatchingObstacleIsAnObstacle() {
        assertThat(oceanFloor.isObstacle(new Position2D(3, 3))).isTrue();
    }

    @Test
    void positionNotMatchingAnyObstacleIsNotAnObstacle() {
        assertThat(oceanFloor.isObstacle(new Position2D(4, 4))).isFalse();
    }
}
