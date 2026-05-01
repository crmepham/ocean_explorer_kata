package org.kata.service.probe;

import org.kata.domain.Direction;

public record ProbeResponse(Long id, Long gridId, PositionResponse position, Direction direction) {
}
