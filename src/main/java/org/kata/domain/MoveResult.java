package org.kata.domain;

public sealed interface MoveResult {
    record Success() implements MoveResult {}
    record BoundaryBlocked() implements MoveResult {}
    record ObstacleBlocked() implements MoveResult {}
}
