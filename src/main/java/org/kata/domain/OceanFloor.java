package org.kata.domain;

import java.util.Set;

public class OceanFloor implements Grid {

    private final int width;
    private final int height;
    private final Set<Position2D> obstacles;

    public OceanFloor(int width, int height, Set<Position2D> obstacles) {
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
    }

    @Override
    public boolean isWithinBounds(Position position) {
        Position2D pos = (Position2D) position;
        return pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height;
    }

    @Override
    public boolean isObstacle(Position position) {
        return obstacles.contains(position);
    }
}
