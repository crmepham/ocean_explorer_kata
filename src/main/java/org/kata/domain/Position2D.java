package org.kata.domain;

public record Position2D(int x, int y) implements Position {

    @Override
    public Position translate(MovementVector vector) {
        return new Position2D(x + vector.directionX(), y + vector.directionY());
    }
}
