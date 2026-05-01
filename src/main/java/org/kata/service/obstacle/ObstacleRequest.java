package org.kata.service.obstacle;

import jakarta.validation.constraints.Min;

public record ObstacleRequest(@Min(0) int x, @Min(0) int y) {
}
