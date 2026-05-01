package org.kata.domain;

public interface Grid {
    boolean isWithinBounds(Position position);
    boolean isObstacle(Position position);
}
