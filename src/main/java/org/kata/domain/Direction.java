package org.kata.domain;

public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    private static final Direction[] VALUES = values();

    public Direction turnLeft() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public Direction turnRight() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public MovementVector forwardVector() {
        return switch (this) {
            case NORTH     -> new MovementVector(0, 1);
            case NORTHEAST -> new MovementVector(1, 1);
            case EAST      -> new MovementVector(1, 0);
            case SOUTHEAST -> new MovementVector(1, -1);
            case SOUTH     -> new MovementVector(0, -1);
            case SOUTHWEST -> new MovementVector(-1, -1);
            case WEST      -> new MovementVector(-1, 0);
            case NORTHWEST -> new MovementVector(-1, 1);
        };
    }

    public MovementVector backwardVector() {
        MovementVector fwd = forwardVector();
        return new MovementVector(-fwd.directionX(), -fwd.directionY());
    }
}
