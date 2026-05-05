package org.kata.service.grid;

import org.kata.service.obstacle.ObstacleResponse;
import org.kata.service.probe.ProbeResponse;

import java.util.List;

public record GridStateResponse(Long id, int width, int height, List<ObstacleResponse> obstacles, List<ProbeResponse> probes) {
}
