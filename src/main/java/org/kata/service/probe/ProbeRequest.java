package org.kata.service.probe;

import org.kata.domain.Direction;

public record ProbeRequest(int x, int y, Direction direction) {
}
