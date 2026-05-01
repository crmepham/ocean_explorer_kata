package org.kata.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovementVectorTest {

    @Test
    void storesDeltaValues() {
        MovementVector vector = new MovementVector(1, 0);

        assertThat(vector.directionX()).isEqualTo(1);
        assertThat(vector.directionY()).isEqualTo(0);
    }
}
