package org.kata.service.grid;

import org.kata.service.obstacle.ObstacleResponse;

import java.util.List;

public record GridResponse(Long id, int width, int height, List<ObstacleResponse> obstacles) {
}
